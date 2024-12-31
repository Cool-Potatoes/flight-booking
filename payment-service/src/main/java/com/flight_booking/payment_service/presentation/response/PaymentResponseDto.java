package com.flight_booking.payment_service.presentation.response;

import com.flight_booking.payment_service.domain.model.Payment;
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
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public PaymentResponseDto(Payment payment) {
    this.paymentId = payment.getPaymentId();
    this.bookingId = payment.getBookingId();
    this.fare = payment.getFare();
    this.createdAt = payment.getCreatedAt();
    this.updatedAt = payment.getUpdatedAt();
  }
}
