package com.flight_booking.user_service.infrastructure.auditing;

import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
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
    // 인증된 사용자 정보
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증되지 않는 경우
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of("SYSTEM");
    }

    // 타입 확인
    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails userDetails) {
      return Optional.ofNullable(userDetails.getUsername());
    } else {
      // 예상한 타입이 아닌 경우 로그 기록
      log.warn("Principal CustomUserDetails 인스턴스가 아닙니다: {}", principal);
      return Optional.empty();
    }
  }
}
