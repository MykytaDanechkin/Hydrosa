package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.data.entity.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocatorServiceUnitTest {

    @Mock
    private SignalService signalService;

    @Mock
    private TrackedObjectService trackedObjectService;

    @InjectMocks
    private LocatorService locatorService;

    @Test
    void testLocateSignalAccuracy() {
        Station s1 = Station.builder()
                .id(1L)
                .latitude(BigDecimal.valueOf(50.0))
                .longitude(BigDecimal.valueOf(30.0))
                .build();

        Station s2 = Station.builder()
                .id(2L)
                .latitude(BigDecimal.valueOf(50.0))
                .longitude(BigDecimal.valueOf(30.1))
                .build();

        double strength = 0.36;
        LocalDateTime now = LocalDateTime.now();

        Signal sig1 = Signal.builder()
                .station(s1)
                .azimuth(45.0)
                .strength(strength)
                .receivedAt(now)
                .processed(false)
                .build();

        Signal sig2 = Signal.builder()
                .station(s2)
                .azimuth(315.0)
                .strength(strength)
                .receivedAt(now)
                .processed(false)
                .build();
        when(signalService.getUnprocessed()).thenReturn(List.of(sig1, sig2));
        when(trackedObjectService.findNearestByPrediction(anyDouble(), anyDouble(), anyDouble())).thenReturn(null);
        locatorService.locateSignal();
        verify(signalService).markProcessed(anyList());
        ArgumentCaptor<Double> latCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> lonCaptor = ArgumentCaptor.forClass(Double.class);
        verify(trackedObjectService).createNew(latCaptor.capture(), lonCaptor.capture(), anyInt(), any());
        assertEquals(50.032, latCaptor.getValue(), 0.001);
        assertEquals(30.05, lonCaptor.getValue(), 0.001);
    }
}
