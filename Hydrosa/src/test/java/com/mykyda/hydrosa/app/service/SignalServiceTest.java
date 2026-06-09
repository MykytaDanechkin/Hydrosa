package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.DTO.create.SignalCreateDTO;
import com.mykyda.hydrosa.app.DTO.demo.SignalViewDTO;
import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.repository.SignalRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalServiceTest {

    @Mock
    private SignalRepository signalRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SignalService signalService;

    @Test
    void testGetAll() {
        when(signalRepository.findAll()).thenReturn(List.of(new Signal()));
        List<Signal> result = signalService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllByStationId() {
        Signal signal = Signal.builder()
                .azimuth(123.0)
                .strength(0.8)
                .build();
        when(signalRepository.findAllByStationId(1L)).thenReturn(List.of(signal));
        
        List<SignalViewDTO> result = signalService.getAllByStationId(1L);
        
        assertEquals(1, result.size());
        assertEquals(123.0, result.get(0).azimuth());
    }

    @Test
    void testSave() {
        SignalCreateDTO dto = SignalCreateDTO.builder()
                .stationId(1L)
                .azimuth(45.0)
                .strength(0.5)
                .build();
        
        Station station = new Station();
        when(entityManager.getReference(Station.class, 1L)).thenReturn(station);
        
        signalService.save(dto);
        
        verify(signalRepository).save(any(Signal.class));
    }

    @Test
    void testGetLatestByStationId() {
        Signal signal = Signal.builder()
                .azimuth(90.0)
                .strength(0.9)
                .build();
        when(signalRepository.findAllByStationIdAndReceivedAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(signal));
        
        List<SignalViewDTO> result = signalService.getLatestByStationId(1L);
        
        assertEquals(1, result.size());
        assertEquals(90.0, result.get(0).azimuth());
    }

    @Test
    void testMarkProcessed() {
        Signal s1 = Signal.builder().id(UUID.randomUUID()).build();
        Signal s2 = Signal.builder().id(UUID.randomUUID()).build();
        
        signalService.markProcessed(List.of(s1, s2));
        
        verify(signalRepository).markProcessed(anyList());
    }
}
