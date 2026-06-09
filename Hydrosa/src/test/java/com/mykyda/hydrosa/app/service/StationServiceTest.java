package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.repository.StationRepository;
import com.mykyda.hydrosa.app.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    void testGetById() {
        Station station = new Station();
        station.setId(1L);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        
        Station result = stationService.getById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetByIdNotFound() {
        when(stationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stationService.getById(1L));
    }

    @Test
    void testGetAll() {
        when(stationRepository.findAll()).thenReturn(List.of(new Station()));
        List<Station> result = stationService.getAll();
        assertEquals(1, result.size());
    }
}
