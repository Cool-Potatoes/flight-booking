package com.flight_booking.payment_service.domain.model;

import com.flight_booking.common.domain.model.PaymentStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_payment")
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID paymentId;

  @Column(nullable = false)
  private UUID bookingId;

  @Column(nullable = false)
  private Long fare;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private PaymentStatusEnum status;

  public Payment updateFare(Long fare) {
    this.fare = fare;
    return this;
  }

  public Payment updateStatus(PaymentStatusEnum status) {
    this.status = status;
    return this;
  }



  public void delete() {
    this.deletedAt = LocalDateTime.now();
    this.isDeleted = true;
  }

}
