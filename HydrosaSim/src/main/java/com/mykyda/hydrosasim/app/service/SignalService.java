package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import com.mykyda.hydrosasim.app.data.repository.SignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository signalRepository;

    @Transactional(readOnly = true)
    public List<Signal> findAllByObjectId(UUID id) {
        return signalRepository.findAllByWaterObjectId(id);
    }

    @Transactional
    public Signal save(Signal signal){
        return signalRepository.save(signal);
    }

    @Transactional
    public List<Signal> save(List<Signal> signal){
        return signalRepository.saveAll(signal);
    }
}
