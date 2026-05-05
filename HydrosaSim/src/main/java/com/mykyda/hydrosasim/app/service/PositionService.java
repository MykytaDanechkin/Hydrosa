package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.data.repository.WaterObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final double EARTH_RADIUS = 6371000;

    private final double TIME_STEP = 0.5;

    private final WaterObjectRepository waterObjectRepository;

    @Scheduled(fixedRateString = "${app.schedules.move-freq}")
    @Transactional
    public void moveObject() {
        waterObjectRepository.findAll().stream()
                .filter(WaterObject::isActive)
                .forEach(obj -> {

                    var speed = obj.getSpeed();
                    var direction = obj.getDirection();

                    var bearingRad = Math.toRadians(direction);

                    var distance = speed * TIME_STEP;

                    var latRad = Math.toRadians(obj.getLatitude().doubleValue());
                    var lonRad = Math.toRadians(obj.getLongitude().doubleValue());

                    var newLat = Math.asin(
                            Math.sin(latRad) * Math.cos(distance / EARTH_RADIUS) +
                                    Math.cos(latRad) * Math.sin(distance / EARTH_RADIUS) * Math.cos(bearingRad)
                    );

                    var newLon = lonRad + Math.atan2(
                            Math.sin(bearingRad) * Math.sin(distance / EARTH_RADIUS) * Math.cos(latRad),
                            Math.cos(distance / EARTH_RADIUS) - Math.sin(latRad) * Math.sin(newLat)
                    );

                    obj.setLatitude(BigDecimal.valueOf(Math.toDegrees(newLat)));
                    obj.setLongitude(BigDecimal.valueOf(Math.toDegrees(newLon)));
                });
    }
}
