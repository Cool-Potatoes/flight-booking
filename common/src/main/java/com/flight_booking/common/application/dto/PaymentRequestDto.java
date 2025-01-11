package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record PaymentRequestDto(
    @NotNull(message = "User Email cannot be null") String email,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Fare cannot be null") @Positive(message = "Fare must be positive") Long fare) {

}

