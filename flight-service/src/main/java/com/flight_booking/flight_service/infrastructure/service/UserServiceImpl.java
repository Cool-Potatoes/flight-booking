package com.flight_booking.flight_service.infrastructure.service;

import com.flight_booking.flight_service.application.service.UserService;
import com.flight_booking.flight_service.infrastructure.feign.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserClient userClient;

  @Override
  @CircuitBreaker(name = "userService-refundMileage",fallbackMethod = "fallbackRefundMileage")
  public Boolean refundMileage(String email, String role, String userEmail, Long paymentFair) {

    return userClient.RefundMileage(email, role, userEmail, paymentFair);
  }

  // Fallback 메서드
  public Boolean fallbackRefundMileage(String email, String role, String userEmail, Long paymentFair, Throwable t) {
    log.warn("CircuitBreaker activated for user email: {}. Fallback triggered.", userEmail, t);
    return false; // 기본값으로 false 반환
  }
}