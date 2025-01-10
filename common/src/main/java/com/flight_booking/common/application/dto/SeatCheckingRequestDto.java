package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SeatCheckingRequestDto(
    @NotNull(message = "Seat ID cannot be null") UUID seatId,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Booking ID cannot be null") PassengerRequestDto passengerRequestDto
) {

}