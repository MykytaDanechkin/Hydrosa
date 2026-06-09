package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = StationController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StationService stationService;

    @Test
    void testGetById() throws Exception {
        when(stationService.getById(1L)).thenReturn(new Station());
        
        mockMvc.perform(get("/station/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAll() throws Exception {
        when(stationService.getAll()).thenReturn(List.of(new Station()));
        
        mockMvc.perform(get("/station"))
                .andExpect(status().isOk());
    }
}
