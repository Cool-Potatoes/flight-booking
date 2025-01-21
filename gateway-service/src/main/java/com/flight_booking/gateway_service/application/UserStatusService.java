package com.flight_booking.gateway_service.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserStatusService {

  private final WebClient webClient;

  public UserStatusDto getUserStatus(String email) {
    try {
      return webClient.get()
          .uri("/v1/users/status/{email}", email)
          .retrieve()
          .bodyToMono(UserStatusDto.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new RuntimeException("Failed to fetch user status: " + e.getMessage(), e);
    }
  }
}
