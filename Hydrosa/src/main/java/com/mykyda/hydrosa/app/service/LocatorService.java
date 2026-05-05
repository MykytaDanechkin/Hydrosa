package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.entity.TrackStatus;
import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocatorService {

    private static final double MAX_OBJECT_DISTANCE  = 15_000;
    private static final double MIN_ANGLE_DIFF       = 45.0;
    private static final double DEDUP_ANGLE          = 10.0;
    private static final double MIN_STRENGTH         = 0.1;
    private static final double MIN_STRENGTH_PRODUCT = 0.05;
    private static final double CLUSTER_RADIUS       = 800;
    private static final int    MIN_CLUSTER_SIZE     = 1;
    private static final double ASSOCIATION_RADIUS   = 2_000;
    private static final double LOST_TIMEOUT_SEC     = 15;
    private static final double EMA_ALPHA            = 0.15;

    private final SignalService        signalService;
    private final TrackedObjectService trackedObjectService;

    @Transactional
    @Scheduled(fixedRate = 1000)
    public void locateSignal() {

        List<Signal> raw = signalService.getUnprocessed();
        if (raw.isEmpty()) return;
        signalService.markProcessed(raw);

        List<Signal> signals = deduplicateByStationAndAzimuth(raw);
        log.info("raw={} dedup={}", raw.size(), signals.size());

        List<WeightedPoint> candidates = new ArrayList<>();

        for (int i = 0; i < signals.size(); i++) {
            for (int j = i + 1; j < signals.size(); j++) {
                Signal s1 = signals.get(i);
                Signal s2 = signals.get(j);

                if (s1.getStation().getId().equals(s2.getStation().getId())) continue;
                if (s1.getStrength() < MIN_STRENGTH || s2.getStrength() < MIN_STRENGTH) continue;

                double weight = s1.getStrength() * s2.getStrength();
                if (weight < MIN_STRENGTH_PRODUCT) continue;

                double angleDiff = Math.abs(s1.getAzimuth() - s2.getAzimuth());
                if (angleDiff > 180) angleDiff = 360 - angleDiff;
                if (angleDiff < MIN_ANGLE_DIFF) continue;

                Station st1 = s1.getStation();
                Station st2 = s2.getStation();

                double[] p = intersectBearings(
                        st1.getLatitude().doubleValue(), st1.getLongitude().doubleValue(), s1.getAzimuth(),
                        st2.getLatitude().doubleValue(), st2.getLongitude().doubleValue(), s2.getAzimuth()
                );
                if (p == null) continue;

                double lat = p[0], lon = p[1];

                double d1 = GeoUtils.distance(st1.getLatitude().doubleValue(), st1.getLongitude().doubleValue(), lat, lon);
                double d2 = GeoUtils.distance(st2.getLatitude().doubleValue(), st2.getLongitude().doubleValue(), lat, lon);
                if (d1 > MAX_OBJECT_DISTANCE || d2 > MAX_OBJECT_DISTANCE) continue;

                double expectedD1 = -5000.0 * Math.log(s1.getStrength());
                double expectedD2 = -5000.0 * Math.log(s2.getStrength());
                if (Math.abs(d1 - expectedD1) > expectedD1 * 0.5) continue;
                if (Math.abs(d2 - expectedD2) > expectedD2 * 0.5) continue;

                LocalDateTime detectedAt = s1.getReceivedAt().isAfter(s2.getReceivedAt())
                        ? s1.getReceivedAt() : s2.getReceivedAt();

                candidates.add(new WeightedPoint(lat, lon, weight, detectedAt));
            }
        }

        log.info("candidates={}", candidates.size());
        if (candidates.isEmpty()) return;

        List<List<WeightedPoint>> clusters = cluster(candidates);
        log.info("clusters={} sizes={}", clusters.size(), clusters.stream().map(List::size).toList());

        trackedObjectService.markLostIfStale(LOST_TIMEOUT_SEC);

        for (List<WeightedPoint> cluster : clusters) {
            if (cluster.size() < MIN_CLUSTER_SIZE) continue;

            double[] centroid = weightedCentroid(cluster);
            double lat = centroid[0], lon = centroid[1];

            LocalDateTime clusterTime = cluster.stream()
                    .map(p -> p.detectedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            TrackedObject match = trackedObjectService.findNearestByPrediction(lat, lon, ASSOCIATION_RADIUS);

            if (match != null) {
                updateTrackedObject(match, lat, lon, clusterTime);
                trackedObjectService.save(match);
                log.info("updated {} -> {},{} cluster={}", match.getId(), lat, lon, cluster.size());
            } else {
                trackedObjectService.createNew(lat, lon, cluster.size(), clusterTime);
                log.info("created new -> {},{} cluster={}", lat, lon, cluster.size());
            }
        }
    }

    private void updateTrackedObject(TrackedObject obj, double newLat, double newLon, LocalDateTime detectedAt) {
        double dt = Duration.between(obj.getLastSeen(), detectedAt).toMillis() / 1000.0;

        if (dt > 0 && dt < 60) {
            double fromLat = obj.getLatitude().doubleValue();
            double fromLon = obj.getLongitude().doubleValue();
            double dist    = GeoUtils.distance(fromLat, fromLon, newLat, newLon);

            if (dist > 0.3) {
                double speed   = dist / dt;
                double bearing = GeoUtils.bearing(fromLat, fromLon, newLat, newLon);

                if (obj.getEstimatedSpeed() == null) {
                    obj.setEstimatedSpeed(speed);
                    obj.setEstimatedDirection(bearing);
                } else {
                    double speedRatio = speed / obj.getEstimatedSpeed();
                    if (speedRatio > 0.3) {
                        obj.setEstimatedSpeed(    EMA_ALPHA * speed   + (1 - EMA_ALPHA) * obj.getEstimatedSpeed());
                        obj.setEstimatedDirection(EMA_ALPHA * bearing + (1 - EMA_ALPHA) * obj.getEstimatedDirection());
                    }
                }
            }
        }

        obj.setLatitude(BigDecimal.valueOf(newLat));
        obj.setLongitude(BigDecimal.valueOf(newLon));
        obj.setLastSeen(detectedAt);
        obj.setDetectionCount(obj.getDetectionCount() + 1);
        obj.setConfidence(Math.min(1.0, obj.getDetectionCount() / 10.0));
        obj.setStatus(TrackStatus.CONFIRMED);
    }

    private static class WeightedPoint {
        final double lat, lon, weight;
        final LocalDateTime detectedAt;

        WeightedPoint(double lat, double lon, double weight, LocalDateTime detectedAt) {
            this.lat = lat;
            this.lon = lon;
            this.weight = weight;
            this.detectedAt = detectedAt;
        }
    }

    private List<Signal> deduplicateByStationAndAzimuth(List<Signal> raw) {
        Map<Long, List<Signal>> byStation = raw.stream()
                .collect(Collectors.groupingBy(s -> s.getStation().getId()));

        List<Signal> result = new ArrayList<>();

        for (List<Signal> stationSignals : byStation.values()) {
            stationSignals.sort(Comparator.comparing(Signal::getReceivedAt).reversed());
            List<Signal> kept = new ArrayList<>();

            for (Signal s : stationSignals) {
                if (s.getStrength() < MIN_STRENGTH) continue;
                boolean duplicate = kept.stream().anyMatch(k -> {
                    double diff = Math.abs(k.getAzimuth() - s.getAzimuth());
                    if (diff > 180) diff = 360 - diff;
                    return diff < DEDUP_ANGLE;
                });
                if (!duplicate) kept.add(s);
            }

            result.addAll(kept);
        }

        return result;
    }

    private double[] intersectBearings(
            double lat1, double lon1, double az1,
            double lat2, double lon2, double az2) {

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double cos1 = Math.cos(Math.toRadians(az1));
        double sin1 = Math.sin(Math.toRadians(az1));
        double cos2 = Math.cos(Math.toRadians(az2));
        double sin2 = Math.sin(Math.toRadians(az2));

        double cosLat = Math.cos((phi1 + phi2) / 2);

        double dNorth = (lat2 - lat1) * 111_320.0;
        double dEast  = (lon2 - lon1) * 111_320.0 * cosLat;

        double det = sin1 * (-cos2) - cos1 * (-sin2);
        if (Math.abs(det) < 1e-10) return null;

        double t1 = (dEast * (-cos2) - dNorth * (-sin2)) / det;
        double t2 = (sin1 * dNorth  - cos1 * dEast)      / det;

        if (t1 < 0 || t2 < 0) return null;

        double latI = lat1 + (t1 * cos1) / 111_320.0;
        double lonI = lon1 + (t1 * sin1) / (111_320.0 * cosLat);

        return new double[]{latI, lonI};
    }

    private List<List<WeightedPoint>> cluster(List<WeightedPoint> points) {
        List<List<WeightedPoint>> clusters = new ArrayList<>();

        for (WeightedPoint p : points) {
            double bestDist = Double.MAX_VALUE;
            List<WeightedPoint> best = null;

            for (List<WeightedPoint> c : clusters) {
                double[] centroid = weightedCentroid(c);
                double d = GeoUtils.distance(centroid[0], centroid[1], p.lat, p.lon);
                if (d < CLUSTER_RADIUS && d < bestDist) {
                    bestDist = d;
                    best = c;
                }
            }

            if (best != null) best.add(p);
            else { List<WeightedPoint> nc = new ArrayList<>(); nc.add(p); clusters.add(nc); }
        }

        return clusters;
    }

    private double[] weightedCentroid(List<WeightedPoint> cluster) {
        double totalWeight = cluster.stream().mapToDouble(p -> p.weight).sum();
        if (totalWeight == 0) {
            return new double[]{
                    cluster.stream().mapToDouble(p -> p.lat).average().orElse(0),
                    cluster.stream().mapToDouble(p -> p.lon).average().orElse(0)
            };
        }
        return new double[]{
                cluster.stream().mapToDouble(p -> p.lat * p.weight).sum() / totalWeight,
                cluster.stream().mapToDouble(p -> p.lon * p.weight).sum() / totalWeight
        };
    }
}