package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.Station;
import com.mykyda.hydrosa.app.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/station")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping("/{id}")
    public Station getById(@PathVariable long id) {
        return stationService.getById(id);
    }

    @GetMapping
    public List<Station> getAll() {
        return stationService.getAll();
    }
}