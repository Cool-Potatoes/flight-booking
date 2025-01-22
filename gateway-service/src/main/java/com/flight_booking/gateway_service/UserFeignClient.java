package com.flight_booking.gateway_service;

import com.flight_booking.gateway_service.application.UserStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

  @GetMapping("/v1/users/status/{email}")
  UserStatusDto getUserStatus(@PathVariable("email") String email);
}