package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.service.SignalStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stream")
public class StreamController {

    private final SignalStreamService streamService;

    @PostMapping("/start/{stationId}")
    public void start(@PathVariable Long stationId) {
        streamService.startStreaming(stationId);
    }

    @PostMapping("/stop/{stationId}")
    public void stop(@PathVariable Long stationId) {
        streamService.stopStreaming(stationId);
    }
}