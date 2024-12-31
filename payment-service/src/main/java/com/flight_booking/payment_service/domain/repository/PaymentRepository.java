package com.flight_booking.payment_service.domain.repository;

import com.flight_booking.payment_service.domain.model.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

  boolean existsByBookingId(UUID bookingId);

}
