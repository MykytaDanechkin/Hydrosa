package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.ObjectPositionHistory;
import com.mykyda.hydrosa.app.data.repository.ObjectPositionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectPositionHistoryService {

    private final ObjectPositionHistoryRepository repository;

    @Transactional
    public void record(UUID trackedObjectId, double lat, double lon, LocalDateTime detectedAt) {
        repository.save(ObjectPositionHistory.builder()
                .trackedObjectId(trackedObjectId)
                .latitude(BigDecimal.valueOf(lat))
                .longitude(BigDecimal.valueOf(lon))
                .detectedAt(detectedAt)
                .build());
    }

    public List<ObjectPositionHistory> getHistory(UUID trackedObjectId) {
        return repository.findAllByTrackedObjectIdOrderByDetectedAtAsc(trackedObjectId);
    }
}