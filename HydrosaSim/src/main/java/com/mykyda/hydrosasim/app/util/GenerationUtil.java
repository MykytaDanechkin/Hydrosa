package com.mykyda.hydrosasim.app.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.gen-util")
public class GenerationUtil {

    private Point dotNw;
    private Point dotNe;
    private Point dotSw;
    private Point dotSe;

    private double maxSpeed = 10.0;

    @Data
    public static class Point {
        private double lat;
        private double lon;
    }
}