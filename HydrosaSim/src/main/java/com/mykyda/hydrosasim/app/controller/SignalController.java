package com.mykyda.hydrosasim.app.controller;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import com.mykyda.hydrosasim.app.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/signals")
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @GetMapping("/{id}")
    public List<Signal> getAllSignalsByObjectId(@PathVariable UUID id){
        return signalService.findAllByObjectId(id);
    }
}
