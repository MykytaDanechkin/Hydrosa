package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.ObjectPositionHistory;
import com.mykyda.hydrosa.app.service.ObjectPositionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ObjectPositionHistoryService objectPositionHistoryService;

    @GetMapping("/{id}")
    public List<ObjectPositionHistory> getHistoryByObjId(@PathVariable UUID id) {
        return objectPositionHistoryService.getHistory(id);
    }

}
