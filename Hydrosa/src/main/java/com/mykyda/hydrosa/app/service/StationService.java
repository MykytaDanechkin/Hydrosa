package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.repository.StationRepository;
import com.mykyda.hydrosa.app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public Station getById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No station with such id " + id));
    }

    @Transactional(readOnly = true)
    public List<Station> getAll() {
        return stationRepository.findAll();
    }
}
