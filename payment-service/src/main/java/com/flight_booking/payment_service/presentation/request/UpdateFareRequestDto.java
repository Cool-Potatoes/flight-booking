package com.flight_booking.payment_service.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record UpdateFareRequestDto(
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Previous Fare cannot be null") @Positive(message = "Fare must be positive") Long previousFare,
    @NotNull(message = "Update Fare cannot be null") @Positive(message = "Fare must be positive") Long newFare) {

}

