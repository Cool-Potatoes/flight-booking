package com.flight_booking.common.application.dto;

import com.flight_booking.common.domain.model.PaymentStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record ProcessPaymentRequestDto(
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Fair cannot be null") Long fair,
    @NotNull(message = "Status cannot be null") @Positive(message = "Status must be positive") PaymentStatusEnum statusEnum) {

}