package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.DTO.create.SignalCreateDTO;
import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.service.SignalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = SignalController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class SignalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignalService signalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        when(signalService.getAll()).thenReturn(List.of(new Signal()));
        
        mockMvc.perform(get("/api/signals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSave() throws Exception {
        SignalCreateDTO dto = SignalCreateDTO.builder()
                .stationId(1L)
                .azimuth(45.0)
                .strength(0.5)
                .build();
        
        mockMvc.perform(post("/api/signals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        
        verify(signalService).save(any(SignalCreateDTO.class));
    }

    @Test
    void testDelete() throws Exception {
        UUID id = UUID.randomUUID();
        
        mockMvc.perform(delete("/api/signals/" + id))
                .andExpect(status().isNoContent());
        
        verify(signalService).delete(id);
    }
}
