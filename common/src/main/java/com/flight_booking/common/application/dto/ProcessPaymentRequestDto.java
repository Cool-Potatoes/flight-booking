package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProcessPaymentRequestDto(
    @NotNull(message = "Payment ID cannot be null") UUID paymentId) {

}