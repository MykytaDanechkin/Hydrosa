package com.mykyda.hydrosasim.app.controller;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import com.mykyda.hydrosasim.app.service.StationService;
import com.mykyda.hydrosasim.app.service.WaterObjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaterObjectController.class)
class WaterObjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WaterObjectService waterObjectService;

    @MockitoBean
    private StationService stationService;

    @Test
    void testGetAll() throws Exception {
        WaterObject obj = WaterObject.builder()
                .id(UUID.randomUUID())
                .latitude(BigDecimal.valueOf(55.0))
                .longitude(BigDecimal.valueOf(12.0))
                .build();
        when(waterObjectService.getAllObjects()).thenReturn(List.of(obj));

        mockMvc.perform(get("/objects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].latitude").value(55.0));
    }

    @Test
    void testGenSignal() throws Exception {
        UUID id = UUID.randomUUID();
        when(waterObjectService.generateRandomObject()).thenReturn(id);

        mockMvc.perform(post("/objects/gen"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + id + "\""));
    }

    @Test
    void testGetMap() throws Exception {
        when(stationService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/objects/map"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }
}
