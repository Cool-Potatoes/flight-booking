package com.flight_booking.gateway_service.infrastructure.feign;

import com.flight_booking.gateway_service.application.dto.UserStatusDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserFallback implements UserFeignClient {

  @Override
  public UserStatusDto getUserStatus(String email) {
    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "사용자 상태를 가져올 수 없습니다.");
  }
}
