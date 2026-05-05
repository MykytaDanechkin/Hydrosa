package com.mykyda.hydrosasim.app.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignalRequest {

    private Long stationId;
    private double azimuth;
    private double strength;
}