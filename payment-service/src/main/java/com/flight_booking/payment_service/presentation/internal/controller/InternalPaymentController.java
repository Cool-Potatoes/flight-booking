package com.flight_booking.payment_service.presentation.internal.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class InternalPaymentController {

  private final PaymentService paymentService;

  @GetMapping("/{bookingId}")
  public Long getPaymentFairByBookingId(
      @PathVariable UUID bookingId) {

    return paymentService.getPaymentFairByBookingId(bookingId);
  }
}
