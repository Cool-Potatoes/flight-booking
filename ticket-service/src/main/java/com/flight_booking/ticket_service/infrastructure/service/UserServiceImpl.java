package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.ticket_service.application.service.UserService;
import com.flight_booking.ticket_service.infrastructure.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserClient userClient;

  @Override
  public Boolean RefundMileage(String email, String role, String userEmail, Long paymentFair) {

    return userClient.RefundMileage(email, role, userEmail, paymentFair);
  }
}
