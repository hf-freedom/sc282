package com.agriculture.harvest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HarvestDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarvestDeliveryApplication.class, args);
    }
}