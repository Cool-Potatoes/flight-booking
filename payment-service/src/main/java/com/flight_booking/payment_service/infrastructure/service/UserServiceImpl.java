package com.flight_booking.payment_service.infrastructure.service;

import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.payment_service.application.service.user.UserService;
import com.flight_booking.payment_service.infrastructure.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserClient userClient;

  @Override
  public boolean updateMileage(PaymentRetryRequestDto requestDto) {
    return userClient.updateMileage(UserRequestDto.from(requestDto), requestDto.email(), "ROLE_USER");
  }
}
