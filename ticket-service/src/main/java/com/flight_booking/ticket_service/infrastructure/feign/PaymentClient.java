package com.flight_booking.ticket_service.infrastructure.feign;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentClient {

  @GetMapping("/internal/payments/{bookingId}")
  ApiResponse<Long> getPaymentFairByBookingId(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID bookingId);
}
