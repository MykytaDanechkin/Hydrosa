package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.service.TrackedObjectService;
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

@WebMvcTest(value = TrackedObjectController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TrackedObjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrackedObjectService trackedObjectService;

    @Test
    void testGetAll() throws Exception {
        when(trackedObjectService.getAll()).thenReturn(List.of(new TrackedObject()));
        
        mockMvc.perform(get("/trackedObject"))
                .andExpect(status().isOk());
    }
}
