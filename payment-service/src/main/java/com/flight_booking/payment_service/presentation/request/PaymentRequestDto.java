package com.flight_booking.payment_service.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record PaymentRequestDto(
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Fare cannot be null") @Positive(message = "Fare must be positive") Integer fare) {

}

