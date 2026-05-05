package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Station;
import com.mykyda.hydrosasim.app.data.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public List<Station> getAll(){
        return stationRepository.findAll();
    }
}
