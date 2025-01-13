package com.flight_booking.common.application.dto;

import java.util.UUID;

public record FlightCancelRequestDto(
    String email,
    UUID ticketId,
    UUID bookingId,
    UUID passengerId,
    UUID seatId
) {

}
