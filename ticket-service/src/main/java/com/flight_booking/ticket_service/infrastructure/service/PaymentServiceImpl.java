package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.ticket_service.application.service.PaymentService;
import com.flight_booking.ticket_service.infrastructure.feign.PaymentClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final PaymentClient paymentClient;

  @Override
  public Long getPaymentFairByBookingId(String email, String role, UUID bookingId) {

    return paymentClient.getPaymentFairByBookingId(email, role, bookingId);
  }
}
