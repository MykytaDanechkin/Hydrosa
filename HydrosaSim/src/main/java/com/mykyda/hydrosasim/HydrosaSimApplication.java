package com.mykyda.hydrosasim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HydrosaSimApplication {

    public static void main(String[] args) {
        SpringApplication.run(HydrosaSimApplication.class, args);
    }

}
