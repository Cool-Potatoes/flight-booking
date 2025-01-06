package com.flight_booking.user_service.infrastructure.security.authentication;

import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j(topic = "사용자 인증 필터")
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    log.info("--------user-service------------");
    String email = request.getHeader("X-USER-Email");
    String role = request.getHeader("X-USER-Role");
    log.info("header: {}, {}", email, role);

    if (email != null && role != null) {
      try {
        // 이메일로 사용자 정보 로드
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // 인증 객체 생성 (권한 포함)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        log.info("auth {}", authentication.getAuthorities());

        // 인증 정보를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        // 사용자 정보 로드 실패 시, 에러 처리
        log.error("사용자 인증 실패: {}", e.getMessage());

        ErrorCode errorCode = ErrorCode.USER_AUTHENTICATION_FAILED;
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().write(errorCode.getMessage());
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}