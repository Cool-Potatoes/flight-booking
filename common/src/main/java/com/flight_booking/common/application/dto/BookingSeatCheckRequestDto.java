package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookingSeatCheckRequestDto(
    @NotNull(message = "Check cannot be null") Boolean check,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Booking ID cannot be null") PassengerRequestDto passengerRequestDto
) {

}
