package com.flight_booking.ticket_service.infrastructure.configuration;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> {
      //TODO : 추후 유저 구현되면 수정
      String userId = "tmpUser";

      return Optional.of(userId);
    };
  }
}
