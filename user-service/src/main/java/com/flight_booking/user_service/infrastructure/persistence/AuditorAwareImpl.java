package com.flight_booking.user_service.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    // 현재 인증된 사용자 정보를 SecurityContext에서 가져옴
    String currentUser = SecurityContextHolder.getContext().getAuthentication() != null ?
        SecurityContextHolder.getContext().getAuthentication().getName() : "Anonymous";

    // 인증되지 않은 사용자
    return Optional.ofNullable(currentUser);
  }
}
