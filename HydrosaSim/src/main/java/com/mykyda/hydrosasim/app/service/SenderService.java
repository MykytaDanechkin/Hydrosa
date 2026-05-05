package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import com.mykyda.hydrosasim.app.data.repository.WaterObjectRepository;
import com.mykyda.hydrosasim.app.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class SenderService {

    private static final double MAX_DISTANCE = 15_000;

    private final WaterObjectRepository waterObjectRepository;

    private final StationService stationService;

    private final SignalService signalService;

    private final QueueService queueService;

    private final java.util.Random random = new java.util.Random();

    @Scheduled(fixedRateString = "${app.schedules.signal-freq}")
    @Transactional
    public void createSignalsAndSend() {

        var stations = stationService.getAll();

        waterObjectRepository.findAll().forEach(obj -> stations.stream()
                .sorted(Comparator.comparingDouble(s -> GeoUtils.distance(
                        obj.getLatitude().doubleValue(), obj.getLongitude().doubleValue(),
                        s.getLatitude().doubleValue(), s.getLongitude().doubleValue()
                )))
                .limit(2)
                .forEach(station -> {

                    double distance = GeoUtils.distance(
                            obj.getLatitude().doubleValue(), obj.getLongitude().doubleValue(),
                            station.getLatitude().doubleValue(), station.getLongitude().doubleValue());

                    if (distance > MAX_DISTANCE) return;

                    double azimuth = GeoUtils.bearing(
                            station.getLatitude().doubleValue(), station.getLongitude().doubleValue(),
                            obj.getLatitude().doubleValue(), obj.getLongitude().doubleValue());

//                    azimuth += random.nextGaussian() * 0.2;

                    double strength = Math.exp(-distance / 5000);

                    if (random.nextDouble() < 0.2) return;

                    Signal signal = Signal.builder()
                            .azimuth(azimuth)
                            .strength(strength)
                            .stationId(station.getId())
                            .waterObject(obj)
                            .sentAt(LocalDateTime.now())
                            .build();

                    signalService.save(signal);

                    queueService.enqueue(signal);
                }));
    }
}