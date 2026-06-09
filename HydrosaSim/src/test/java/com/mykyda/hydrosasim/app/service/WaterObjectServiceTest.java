package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.data.repository.WaterObjectRepository;
import com.mykyda.hydrosasim.app.exception.EntityNotFoundException;
import com.mykyda.hydrosasim.app.util.GenerationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaterObjectServiceTest {

    @Mock
    private WaterObjectRepository waterObjectRepository;

    @Mock
    private GenerationUtil genUtil;

    @InjectMocks
    private WaterObjectService waterObjectService;

    @Test
    void testGetAllObjects() {
        when(waterObjectRepository.findAll()).thenReturn(List.of(new WaterObject()));
        List<WaterObject> result = waterObjectService.getAllObjects();
        assertEquals(1, result.size());
    }

    @Test
    void testGetObject_Found() {
        UUID id = UUID.randomUUID();
        WaterObject obj = new WaterObject();
        when(waterObjectRepository.findById(id)).thenReturn(Optional.of(obj));
        WaterObject result = waterObjectService.getObject(id);
        assertNotNull(result);
    }

    @Test
    void testGetObject_NotFound() {
        UUID id = UUID.randomUUID();
        when(waterObjectRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> waterObjectService.getObject(id));
    }

    @Test
    void testGenerateRandomObject() {
        GenerationUtil.Point p1 = new GenerationUtil.Point();
        p1.setLat(55.0); p1.setLon(12.0);
        GenerationUtil.Point p2 = new GenerationUtil.Point();
        p2.setLat(57.0); p2.setLon(13.0);

        when(genUtil.getDotSw()).thenReturn(p1);
        when(genUtil.getDotSe()).thenReturn(p2);
        when(genUtil.getDotNw()).thenReturn(p1);
        when(genUtil.getDotNe()).thenReturn(p2);
        when(genUtil.getMaxSpeed()).thenReturn(10.0);

        WaterObject saved = new WaterObject();
        saved.setId(UUID.randomUUID());
        when(waterObjectRepository.save(any(WaterObject.class))).thenReturn(saved);

        UUID resultId = waterObjectService.generateRandomObject();
        assertEquals(saved.getId(), resultId);
        verify(waterObjectRepository).save(any(WaterObject.class));
    }
}
