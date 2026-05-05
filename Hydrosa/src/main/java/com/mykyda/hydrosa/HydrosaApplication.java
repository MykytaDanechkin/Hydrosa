package com.mykyda.hydrosa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HydrosaApplication {
    public static void main(String[] args) {
        SpringApplication.run(HydrosaApplication.class, args);
    }
}
