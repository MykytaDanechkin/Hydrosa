package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.data.entity.ObjectPositionHistory;
import com.mykyda.hydrosa.app.data.repository.ObjectPositionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectPositionHistoryServiceTest {

    @Mock
    private ObjectPositionHistoryRepository repository;

    @InjectMocks
    private ObjectPositionHistoryService historyService;

    @Test
    void testRecord() {
        UUID objId = UUID.randomUUID();
        historyService.record(objId, 56.0, 12.0, LocalDateTime.now());
        verify(repository).save(any(ObjectPositionHistory.class));
    }

    @Test
    void testGetHistory() {
        UUID objId = UUID.randomUUID();
        when(repository.findAllByTrackedObjectIdOrderByDetectedAtAsc(objId))
                .thenReturn(List.of(new ObjectPositionHistory()));
        
        List<ObjectPositionHistory> history = historyService.getHistory(objId);
        assertEquals(1, history.size());
    }
}
