package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record PaymentRetryRequestDto(
    @NotNull(message = "User Email cannot be null") String email,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Fare cannot be null") @Positive(message = "Fare must be positive") Long fare,
    int retryCount
) {

  public static PaymentRetryRequestDto from(
      PaymentRetryRequestDto paymentRetryRequestDto, int retryCount) {

    return new PaymentRetryRequestDto(
        paymentRetryRequestDto.email,
        paymentRetryRequestDto.bookingId,
        paymentRetryRequestDto.fare,
        retryCount
    );
  }

}

