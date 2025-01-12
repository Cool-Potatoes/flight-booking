package com.flight_booking.ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.flight_booking.ticket_service", "com.flight_booking.common"})
public class TicketServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TicketServiceApplication.class, args);
  }

}
