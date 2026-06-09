package com.mykyda.hydrosasim.app.controller;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import com.mykyda.hydrosasim.app.service.SignalService;
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

@WebMvcTest(SignalController.class)
class SignalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignalService signalService;

    @Test
    void testGetAllSignalsByObjectId() throws Exception {
        UUID id = UUID.randomUUID();
        when(signalService.findAllByObjectId(id)).thenReturn(List.of(new Signal()));

        mockMvc.perform(get("/api/signals/" + id))
                .andExpect(status().isOk());
    }
}
