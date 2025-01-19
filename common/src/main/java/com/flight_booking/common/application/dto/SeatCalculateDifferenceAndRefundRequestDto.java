package com.flight_booking.common.application.dto;

import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import java.util.UUID;

public record SeatCalculateDifferenceAndRefundRequestDto(
    String email,
    String role,
    UUID ticketId,
    UUID bookingId,
    UUID seatId,
    UUID passengerId,
    PassengerRequestDto passengerRequestDto
) {

  public static SeatCalculateDifferenceAndRefundRequestDto from(CustomUserDetails userDetails,
      UUID ticketId, UUID bookingId, UUID seatId, UUID passengerId,
      PassengerRequestDto passengerRequestDto) {
    return new SeatCalculateDifferenceAndRefundRequestDto(
        userDetails.email(),
        userDetails.role(),
        ticketId,
        bookingId,
        seatId,
        passengerId,
        passengerRequestDto
    );
  }
}
