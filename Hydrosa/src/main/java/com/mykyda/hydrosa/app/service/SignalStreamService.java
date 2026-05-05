package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.DTO.demo.SignalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SignalStreamService {

    private final SimpMessagingTemplate messagingTemplate;


    private final SignalService signalService;

    private final Set<Long> activeStations = ConcurrentHashMap.newKeySet();

    public void startStreaming(Long stationId) {
        activeStations.add(stationId);
    }

    public void stopStreaming(Long stationId) {
        activeStations.remove(stationId);
    }

    @Scheduled(fixedRate = 500L)
    public void streamSignals() {
        for (Long stationId : activeStations) {
            var signals = signalService.getLatestByStationId(stationId);
            signals.forEach(s -> messagingTemplate.convertAndSend(
                    "/topic/station/" + stationId,
                    new SignalMessage(s.azimuth(), s.strength())
            ));
        }
    }
}