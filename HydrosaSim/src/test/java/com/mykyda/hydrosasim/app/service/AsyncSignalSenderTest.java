package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncSignalSenderTest {

    @Mock
    private QueueService queueService;

    @Mock
    private RestTemplate restTemplate;

    private AsyncSignalSender asyncSignalSender;

    @BeforeEach
    void setUp() {
        asyncSignalSender = new AsyncSignalSender(queueService, "http://localhost:8080");
        asyncSignalSender.setRestTemplate(restTemplate);
    }

    @Test
    void testProcessQueue_Success() {
        Signal signal = Signal.builder().stationId(1L).azimuth(10.0).strength(0.5).build();
        when(queueService.poll()).thenReturn(signal).thenReturn(null);

        asyncSignalSender.processQueue();

        verify(restTemplate).postForEntity(eq("http://localhost:8080/api/signals"), any(), eq(Void.class));
    }

    @Test
    void testProcessQueue_RetrySuccess() {
        Signal signal = Signal.builder().stationId(1L).azimuth(10.0).strength(0.5).build();
        when(queueService.poll()).thenReturn(signal).thenReturn(null);
        
        when(restTemplate.postForEntity(anyString(), any(), any()))
                .thenThrow(new RuntimeException("Fail"))
                .thenReturn(null);

        asyncSignalSender.processQueue();

        verify(restTemplate, times(2)).postForEntity(anyString(), any(), any());
    }
}
