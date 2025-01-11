package com.flight_booking.ticket_service.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TicketRequestDto(
    @NotNull(message = "Booking ID cannot be null")
    UUID bookingId,
    @NotNull(message = "Passenger ID cannot be null")
    UUID passengerId,
    @NotNull(message = "Seat ID cannot be null")
    UUID seatId,
    @NotNull(message = "Flight ID cannot be null")
    UUID flightId
) {

}