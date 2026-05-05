package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.DTO.create.SignalCreateDTO;
import com.mykyda.hydrosa.app.data.entity.Signal;
import com.mykyda.hydrosa.app.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signals")
public class SignalController {

    private final SignalService signalService;

    @GetMapping
    public List<Signal> getAll() {
        return signalService.getAll();
    }

//    @GetMapping("/{stationId}")
//    public List<SignalViewDTO> getAllByStationId(@PathVariable Long stationId) {
//        return signalService.getAllByStationId(stationId);
//    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody SignalCreateDTO signalDTO) {
        signalDTO.setTimestamp(LocalDateTime.now());
        signalService.save(signalDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        signalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
