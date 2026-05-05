package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.TrackStatus;
import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.data.repository.TrackedObjectRepository;
import com.mykyda.hydrosa.app.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackedObjectService {

    private final TrackedObjectRepository repository;

    public TrackedObject findNearestByPrediction(double lat, double lon, double maxRadius) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByStatusNot((TrackStatus.LOST)).stream()
                .filter(obj -> {
                    double[] predicted = predictPosition(obj, now);
                    return GeoUtils.distance(predicted[0], predicted[1], lat, lon) < maxRadius;
                })
                .min(Comparator.comparingDouble(obj -> {
                    double[] predicted = predictPosition(obj, now);
                    return GeoUtils.distance(predicted[0], predicted[1], lat, lon);
                }))
                .orElse(null);
    }

    public double[] predictPosition(TrackedObject obj, LocalDateTime at) {
        if (obj.getEstimatedSpeed() == null || obj.getEstimatedDirection() == null
                || obj.getLastSeen() == null) {
            return new double[]{
                obj.getLatitude().doubleValue(),
                obj.getLongitude().doubleValue()
            };
        }
        double dt   = Duration.between(obj.getLastSeen(), at).toMillis() / 1000.0;
        double dist = obj.getEstimatedSpeed() * dt;
        return GeoUtils.destinationPoint(
                obj.getLatitude().doubleValue(),
                obj.getLongitude().doubleValue(),
                obj.getEstimatedDirection(),
                dist
        );
    }

    public void save(TrackedObject obj) {
        repository.save(obj);
    }

    @Transactional
    public void markLostIfStale(double timeoutSeconds) {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds((long) timeoutSeconds);
        repository.findByStatusNot(TrackStatus.LOST).forEach(obj -> {
            if (obj.getLastSeen() != null && obj.getLastSeen().isBefore(cutoff)) {
                obj.setStatus(TrackStatus.LOST);
                repository.save(obj);
                log.info("Tracked object {} marked LOST", obj.getId());
            }
        });
    }

    public List<TrackedObject> getAll() {
        return repository.findAll();
    }

    public void createNew(double lat, double lon, int clusterSize, LocalDateTime detectedAt) {
        repository.save(TrackedObject.builder()
                .latitude(BigDecimal.valueOf(lat))
                .longitude(BigDecimal.valueOf(lon))
                .firstSeen(detectedAt)
                .lastSeen(detectedAt)
                .detectionCount(clusterSize)
                .confidence(clusterSize / 10.0)
                .status(TrackStatus.ACTIVE)
                .build());
    }
}