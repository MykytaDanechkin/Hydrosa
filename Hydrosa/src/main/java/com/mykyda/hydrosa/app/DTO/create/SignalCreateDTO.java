package com.mykyda.hydrosa.app.DTO.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalCreateDTO {

    private Long stationId;
    private double azimuth;
    private LocalDateTime timestamp;
    private double strength;
}