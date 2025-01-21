package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.ticket_service.application.service.FlightService;
import com.flight_booking.ticket_service.infrastructure.feign.FlightClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

  private final FlightClient flightClient;

  @Override
  public Boolean checkFlightStatusBySeatId(String email, String role, UUID seatId) {

    return flightClient.checkFlightStatusBySeatId(email, role, seatId);
  }

  @Cacheable(cacheNames = "ticket:update:seat:get:availability", key = "#seatId")
  @Override
  public Boolean getSeatIsAvailable(String email, String role, UUID seatId) {

    return flightClient.getSeatIsAvailable(email, role, seatId);
  }
}
