package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SeatAvailabilityCheckForRebookRequestDto(
    @NotNull(message = "Email cannot be null") String email,
    @NotNull(message = "Ticket Id cannot be null") UUID ticketId,
    @NotNull(message = "Booking Id cannot be null") UUID bookingId,
    @NotNull(message = "Seat Id cannot be null") UUID seatId
    ) {

}