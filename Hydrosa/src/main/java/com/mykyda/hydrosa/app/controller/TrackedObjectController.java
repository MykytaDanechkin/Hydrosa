package com.mykyda.hydrosa.app.controller;

import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import com.mykyda.hydrosa.app.service.TrackedObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trackedObject")
@RequiredArgsConstructor
public class TrackedObjectController {

    private final TrackedObjectService trackedObjectService;

    @GetMapping
    public List<TrackedObject> getAll() {
        return trackedObjectService.getAll();
    }

}
