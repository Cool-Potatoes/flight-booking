package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record PaymentRetryRequestDto(
    @NotNull(message = "User Email cannot be null") String email,
    @NotNull(message = "Payment ID cannot be null") UUID paymentId,
    @NotNull(message = "Fare cannot be null") @Positive(message = "Fare must be positive") Long fare,
    int retryCount
) {

  public static PaymentRetryRequestDto from(
      PaymentRetryRequestDto paymentRetryRequestDto, int retryCount) {

    return new PaymentRetryRequestDto(
        paymentRetryRequestDto.email,
        paymentRetryRequestDto.paymentId,
        paymentRetryRequestDto.fare,
        retryCount
    );
  }

  public static PaymentRetryRequestDto from(String email, UserRequestDto userRequestDto) {

    return new PaymentRetryRequestDto(
        email,
        userRequestDto.paymentId(),
        userRequestDto.fare(),
        0
    );
  }
}

