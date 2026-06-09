package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.service.SignalStreamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = StreamController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignalStreamService streamService;

    @Test
    void testStart() throws Exception {
        mockMvc.perform(post("/stream/start/1"))
                .andExpect(status().isOk());
        
        verify(streamService).startStreaming(1L);
    }

    @Test
    void testStop() throws Exception {
        mockMvc.perform(post("/stream/stop/1"))
                .andExpect(status().isOk());
        
        verify(streamService).stopStreaming(1L);
    }
}
