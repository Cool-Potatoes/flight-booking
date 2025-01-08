package com.flight_booking.user_service.infrastructure.persistence;

import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    // 인증된 사용자 정보
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증되지 않는 경우
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of("SYSTEM");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // CustomUserDetails의 getUsername(email) 사용
    return Optional.of(userDetails.getUsername());
  }
}
