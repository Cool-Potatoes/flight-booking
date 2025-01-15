package com.flight_booking.ticket_service.infrastructure.feign;

import com.flight_booking.common.presentation.global.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

  @GetMapping("/internal/users/{userEmail}")
  ApiResponse<Boolean> checkAndRefundMileage(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable String userEmail,
      @RequestParam Long difference,
      @RequestParam Long paymentFair);

  @GetMapping("/internal/users/cancel/{userEmail}")
  ApiResponse<Boolean> RefundMileage(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable String userEmail,
      @RequestParam Long paymentFair);
}
