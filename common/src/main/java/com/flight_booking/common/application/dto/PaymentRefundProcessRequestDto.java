package com.flight_booking.common.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentRefundProcessRequestDto(
    UUID ticketId,
    @NotNull(message = "Payment ID cannot be null") UUID paymentId,
    @NotNull(message = "Email cannot be null") String email
) {

  public static PaymentRefundProcessRequestDto from(PaymentRetryRequestDto requestDto) {

    return new PaymentRefundProcessRequestDto(null, requestDto.paymentId(), requestDto.email());
  }
}