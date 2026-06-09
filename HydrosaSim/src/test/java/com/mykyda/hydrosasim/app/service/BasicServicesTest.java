package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import com.mykyda.hydrosasim.app.data.entity.Station;
import com.mykyda.hydrosasim.app.data.repository.SignalRepository;
import com.mykyda.hydrosasim.app.data.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicServicesTest {

    @Mock
    private SignalRepository signalRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private SignalService signalService;

    @InjectMocks
    private StationService stationService;

    @Test
    void testSignalService_FindAllByObjectId() {
        UUID id = UUID.randomUUID();
        when(signalRepository.findAllByWaterObjectId(id)).thenReturn(List.of(new Signal()));
        List<Signal> result = signalService.findAllByObjectId(id);
        assertEquals(1, result.size());
    }

    @Test
    void testSignalService_Save() {
        Signal signal = new Signal();
        when(signalRepository.save(signal)).thenReturn(signal);
        Signal result = signalService.save(signal);
        assertEquals(signal, result);
    }

    @Test
    void testStationService_GetAll() {
        when(stationRepository.findAll()).thenReturn(List.of(new Station()));
        List<Station> result = stationService.getAll();
        assertEquals(1, result.size());
    }
}
