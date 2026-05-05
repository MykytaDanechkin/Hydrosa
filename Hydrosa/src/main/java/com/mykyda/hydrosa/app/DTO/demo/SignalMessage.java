package com.mykyda.hydrosa.app.DTO.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignalMessage {
    private double azimuth;
    private double strength;
}