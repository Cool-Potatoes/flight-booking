package com.flight_booking.user_service.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    // 인증된 사용자 정보
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증되지 않은 사용자일 경우 "SYSTEM" 또는 "AnonymousUser"
    if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
      return Optional.of("SYSTEM");
    }

    // 인증된 사용자일 경우 이메일 반환
    return Optional.ofNullable(authentication.getPrincipal().toString());
  }
}
