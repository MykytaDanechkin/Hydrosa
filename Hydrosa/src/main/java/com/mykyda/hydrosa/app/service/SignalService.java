package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.DTO.create.SignalCreateDTO;
import com.mykyda.hydrosa.app.DTO.demo.SignalViewDTO;
import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.data.repository.SignalRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository signalRepository;

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Signal> getAll() {
        return signalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SignalViewDTO> getAllByStationId(Long stationId) {
        return signalRepository.findAllByStationId(stationId)
                .stream()
                .map(obj -> new SignalViewDTO(obj.getAzimuth(), obj.getStrength())).toList();
    }

    @Transactional
    public void save(SignalCreateDTO signalDTO) {
        signalRepository.save(Signal.builder()
                .azimuth(signalDTO.getAzimuth())
                .station(entityManager.getReference(Station.class, signalDTO.getStationId()))
                .strength(signalDTO.getStrength())
                .build());
        log.debug("received: {}", signalDTO);
    }

    @Transactional
    public void delete(UUID id) {
        signalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SignalViewDTO> getLatestByStationId(Long stationId) {
        return
                signalRepository.findAllByStationIdAndReceivedAtAfter(stationId,
                                LocalDateTime.now().minusNanos(10 * 100_000_000L))
                        .stream()
                        .map(obj -> new SignalViewDTO(obj.getAzimuth(), obj.getStrength()))
                        .toList();
    }

    public List<Signal> getUnprocessed() {
        return signalRepository.findAllByProcessedFalse();
    }

    @Transactional
    public void markProcessed(List<Signal> signals) {
        var ids = signals.stream().map(Signal::getId).toList();
        signalRepository.markProcessed(ids);
    }
}
