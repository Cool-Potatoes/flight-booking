package com.flight_booking.payment_service.infrastructure.client;

import com.flight_booking.common.application.dto.UserRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

  @PostMapping("/v1/users/mileage")
  boolean updateMileage(
      @RequestBody UserRequestDto userRequestDto,
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role);

}
