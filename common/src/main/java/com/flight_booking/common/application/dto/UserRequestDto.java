package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record UserRequestDto(
    @NotNull(message = "User Email cannot be null") String email,
    @NotNull(message = "Fair cannot be null") Long fare,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Mileage cannot be null") @Positive(message = "Mileage must be positive") Long Mileage) {

}
