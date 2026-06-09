package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.service.SignalService;
import com.mykyda.hydrosa.app.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = HydroController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class HydroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignalService signalService;

    @MockitoBean
    private StationService stationService;

    @Test
    void testGetMap() throws Exception {
        Station station = Station.builder()
                .id(1L)
                .latitude(BigDecimal.valueOf(56.0))
                .longitude(BigDecimal.valueOf(12.0))
                .build();
        
        when(stationService.getById(1L)).thenReturn(station);
        when(signalService.getAllByStationId(1L)).thenReturn(List.of());
        
        mockMvc.perform(get("/1/map"))
                .andExpect(status().isOk());
    }
}
