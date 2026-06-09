package com.mykyda.hydrosa.app.service;

import com.mykyda.hydrosa.app.DTO.demo.SignalMessage;
import com.mykyda.hydrosa.app.DTO.demo.SignalViewDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalStreamServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SignalService signalService;

    @InjectMocks
    private SignalStreamService signalStreamService;

    @Test
    void testStreamSignals() {
        signalStreamService.startStreaming(1L);
        
        SignalViewDTO signal = new SignalViewDTO(120.0, 0.7);
        when(signalService.getLatestByStationId(1L)).thenReturn(List.of(signal));
        
        signalStreamService.streamSignals();
        
        verify(messagingTemplate).convertAndSend(eq("/topic/station/1"), any(SignalMessage.class));
    }

    @Test
    void testStopStreaming() {
        signalStreamService.startStreaming(1L);
        signalStreamService.stopStreaming(1L);
        
        signalStreamService.streamSignals();
        
        verify(signalService, never()).getLatestByStationId(anyLong());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
}
