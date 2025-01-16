package com.flight_booking.ticket_service.application.service;

import java.util.UUID;

public interface FlightService {

  Long updateSeatAvailableFalseAndGetSeatPrice(String email, String role, UUID seatId);

  Boolean checkFlightStatusBySeatId(String email, String role, UUID seatId);

  Boolean getSeatIsAvailable(String email, String role, UUID seatId);
}
