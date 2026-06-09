package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.ObjectPositionHistory;
import com.mykyda.hydrosa.app.service.ObjectPositionHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = HistoryController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObjectPositionHistoryService historyService;

    @Test
    void testGetHistoryByObjId() throws Exception {
        UUID id = UUID.randomUUID();
        when(historyService.getHistory(id)).thenReturn(List.of(new ObjectPositionHistory()));
        
        mockMvc.perform(get("/history/" + id))
                .andExpect(status().isOk());
    }
}
