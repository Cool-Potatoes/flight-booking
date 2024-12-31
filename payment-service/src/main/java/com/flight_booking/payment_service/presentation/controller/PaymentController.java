package com.flight_booking.payment_service.presentation.controller;

import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.presentation.global.ApiResponse;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ApiResponse<?> createPayment(
      @RequestBody PaymentRequestDto paymentRequestDto) {

    PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);

    return ApiResponse.ok(paymentResponseDto);
  }

}
