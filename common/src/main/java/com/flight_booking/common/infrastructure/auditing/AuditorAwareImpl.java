package com.flight_booking.common.infrastructure.auditing;

import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증되지 않은 경우
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of("SYSTEM");
    }

    // 인증된 경우 CustomUserDetails의 getUsername 이용
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return Optional.ofNullable(userDetails.getUsername());
  }
}
