package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.TrackStatus;
import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.data.repository.TrackedObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackedObjectServiceTest {

    @Mock
    private TrackedObjectRepository repository;

    @InjectMocks
    private TrackedObjectService trackedObjectService;

    private TrackedObject activeObject;

    @BeforeEach
    void setUp() {
        activeObject = TrackedObject.builder()
                .id(UUID.randomUUID())
                .latitude(BigDecimal.valueOf(56.0))
                .longitude(BigDecimal.valueOf(12.0))
                .estimatedSpeed(10.0)
                .estimatedDirection(90.0)
                .lastSeen(LocalDateTime.now().minusSeconds(10))
                .status(TrackStatus.ACTIVE)
                .build();
    }

    @Test
    void testPredictPosition() {
        LocalDateTime now = LocalDateTime.now();
        double[] prediction = trackedObjectService.predictPosition(activeObject, now);
        
        assertNotNull(prediction);
        assertEquals(2, prediction.length);
        // At 90 degrees (East), longitude should increase, latitude should stay roughly same
        assertTrue(prediction[1] > 12.0);
    }

    @Test
    void testFindNearestByPrediction() {
        when(repository.findByStatusNot(TrackStatus.LOST)).thenReturn(List.of(activeObject));
        
        // Target is near the predicted position
        TrackedObject found = trackedObjectService.findNearestByPrediction(56.0, 12.002, 1000.0);
        
        assertNotNull(found);
        assertEquals(activeObject.getId(), found.getId());
    }

    @Test
    void testMarkLostIfStale() {
        activeObject.setLastSeen(LocalDateTime.now().minusSeconds(100));
        when(repository.findByStatusNot(TrackStatus.LOST)).thenReturn(List.of(activeObject));
        
        trackedObjectService.markLostIfStale(60.0);
        
        assertEquals(TrackStatus.LOST, activeObject.getStatus());
        verify(repository).save(activeObject);
    }

    @Test
    void testCreateNew() {
        trackedObjectService.createNew(56.1, 12.1, 5, LocalDateTime.now());
        verify(repository).save(any(TrackedObject.class));
    }
}
