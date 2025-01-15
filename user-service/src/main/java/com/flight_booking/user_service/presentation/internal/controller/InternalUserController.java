package com.flight_booking.user_service.presentation.internal.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
@Slf4j
public class InternalUserController {

  private final UserService userService;

  @GetMapping("/{email}")
  public ApiResponse<?> checkAndRefundMileage(
      @PathVariable String email,
      @RequestParam Long difference,
      @RequestParam Long paymentFair
  ) {
    Boolean bool = userService.checkAndRefundMileage(email, difference, paymentFair);
    return ApiResponse.ok(bool, "마일리지 체크, 환불 완료");
  }

  @GetMapping("/cancel/{email}")
  public ApiResponse<?> RefundMileage(
      @PathVariable String email,
      @RequestParam Long paymentFair
  ) {
    Boolean bool = userService.RefundMileage(email, paymentFair);
    return ApiResponse.ok(bool, "환불 완료");
  }
}
