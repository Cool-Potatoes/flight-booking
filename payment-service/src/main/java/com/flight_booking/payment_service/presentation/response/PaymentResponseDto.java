package com.flight_booking.payment_service.presentation.response;

import com.flight_booking.payment_service.domain.model.Payment;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

  private UUID paymentId;
  private UUID bookingId;
  private Integer fare;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @QueryProjection
  public PaymentResponseDto(Payment payment) {
    this.paymentId = payment.getPaymentId();
    this.bookingId = payment.getBookingId();
    this.fare = payment.getFare();
    this.status = payment.getStatus().toString();
    this.createdAt = payment.getCreatedAt();
    this.updatedAt = payment.getUpdatedAt();
  }
}
