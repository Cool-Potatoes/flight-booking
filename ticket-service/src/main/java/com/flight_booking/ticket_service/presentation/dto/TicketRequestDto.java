package com.flight_booking.ticket_service.presentation.dto;

import java.util.UUID;

public record TicketRequestDto(
    UUID bookingId,
    UUID passengerId,
    UUID seatId,
    UUID flightId
) {
}