package com.mykyda.hydrosasim.app.DTO;

import java.util.UUID;

public record WaterObjectDTO(
        UUID id,
        double latitude,
        double longitude
) {}