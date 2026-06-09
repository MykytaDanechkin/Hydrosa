package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.data.repository.WaterObjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private WaterObjectRepository waterObjectRepository;

    @InjectMocks
    private PositionService positionService;

    @Test
    void testMoveObject() {
        WaterObject obj = WaterObject.builder()
                .latitude(BigDecimal.valueOf(55.0))
                .longitude(BigDecimal.valueOf(12.0))
                .speed(10.0)
                .direction(90.0)
                .active(true)
                .build();

        when(waterObjectRepository.findAll()).thenReturn(List.of(obj));

        positionService.moveObject();

        assertNotEquals(BigDecimal.valueOf(12.0), obj.getLongitude());
    }
}
