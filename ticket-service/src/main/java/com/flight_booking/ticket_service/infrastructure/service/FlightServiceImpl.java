package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.ticket_service.application.service.FlightService;
import com.flight_booking.ticket_service.infrastructure.feign.FlightClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

  private final FlightClient flightClient;

  @Override
  public Long updateSeatAvailableFalseAndGetSeatPrice(String email, String role, UUID seatId) {

    return flightClient.updateSeatAvailableFalseAndGetSeatPrice(email, role, seatId);
  }

  @Override
  public Boolean checkFlightStatusBySeatId(String email, String role, UUID seatId) {

    return flightClient.checkFlightStatusBySeatId(email, role, seatId);
  }
}
