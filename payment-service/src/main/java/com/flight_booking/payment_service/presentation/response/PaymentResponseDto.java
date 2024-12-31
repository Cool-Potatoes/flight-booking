package com.flight_booking.payment_service.presentation.response;

import com.flight_booking.payment_service.domain.model.Payment;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(UUID paymentId, UUID bookingId, Integer fare, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {

  @QueryProjection
  public PaymentResponseDto(Payment payment) {
    this(payment.getPaymentId(), payment.getBookingId(), payment.getFare(), payment.getStatus().toString(), payment.getCreatedAt(), payment.getUpdatedAt());
  }
}
