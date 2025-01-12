package com.flight_booking.flight_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.flight_booking.flight_service", "com.flight_booking.common"})
public class FlightServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FlightServiceApplication.class, args);
  }

}
