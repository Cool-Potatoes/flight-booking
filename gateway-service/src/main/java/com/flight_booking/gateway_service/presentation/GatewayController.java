package com.flight_booking.gateway_service.presentation;

import com.flight_booking.gateway_service.application.UserStatusService;
import com.flight_booking.gateway_service.application.UserStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GatewayController {

  private final UserStatusService userStatusService;

  @GetMapping("/v1/user/status/{email}")
  public UserStatusDto getUserStatus(@PathVariable String email) {
    return userStatusService.getUserStatus(email);
  }
}
