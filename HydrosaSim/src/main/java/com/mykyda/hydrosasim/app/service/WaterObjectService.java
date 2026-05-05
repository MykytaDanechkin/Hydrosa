package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.data.repository.WaterObjectRepository;
import com.mykyda.hydrosasim.app.exception.EntityNotFoundException;
import com.mykyda.hydrosasim.app.util.GenerationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaterObjectService {

    private final WaterObjectRepository waterObjectRepository;

    private final GenerationUtil genUtil;

    private final Random random = new Random();

    @Transactional(readOnly = true)
    public List<WaterObject> getAllObjects(){
        return waterObjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WaterObject getObject(UUID id){
        return waterObjectRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException("WaterObject not found"));
    }

    @Transactional
    public UUID generateRandomObject(){
        var minLat = Math.min(genUtil.getDotSw().getLat(), genUtil.getDotSe().getLat());
        var maxLat = Math.max(genUtil.getDotNw().getLat(), genUtil.getDotNe().getLat());

        var minLon = Math.min(genUtil.getDotSw().getLon(), genUtil.getDotNw().getLon());
        var maxLon = Math.max(genUtil.getDotSe().getLon(), genUtil.getDotNe().getLon());

        var latitude = randomBetween(minLat, maxLat);
        var longitude = randomBetween(minLon, maxLon);

        var speed = randomBetween(0, genUtil.getMaxSpeed());

        var direction = randomBetween(0, 360);

        var object = WaterObject.builder()
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .speed(speed)
                .direction(direction)
                .build();

        return waterObjectRepository.save(object).getId();
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
