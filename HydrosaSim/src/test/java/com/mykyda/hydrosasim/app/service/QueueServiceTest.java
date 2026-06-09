package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueueServiceTest {

    private QueueService queueService;

    @BeforeEach
    void setUp() {
        queueService = new QueueService();
    }

    @Test
    void testEnqueueAndPoll() {
        Signal signal = Signal.builder().azimuth(10.0).build();
        queueService.enqueue(signal);
        
        Signal polled = queueService.poll();
        assertNotNull(polled);
        assertEquals(10.0, polled.getAzimuth());
    }

    @Test
    void testPollEmptyQueue() {
        Signal polled = queueService.poll();
        assertNull(polled);
    }
}
