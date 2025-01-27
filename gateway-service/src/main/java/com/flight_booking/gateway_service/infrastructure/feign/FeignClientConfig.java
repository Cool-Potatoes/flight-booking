package com.flight_booking.gateway_service.infrastructure.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignClientConfig {

  @Value("${service.feign.secret}")
  private String FEIGN_SECRET;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("X-Internal-feign", FEIGN_SECRET);
    };
  }

}
