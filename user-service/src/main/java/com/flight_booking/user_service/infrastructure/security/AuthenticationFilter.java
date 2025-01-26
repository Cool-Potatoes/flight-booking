package com.flight_booking.user_service.infrastructure.security;

import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${service.feign.secret}")
  private String FEIGN_SECRET;

  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // FeignClient 호출을 위한 내부 헤더 체크
    String internalFeignHeader = request.getHeader("X-Internal-feign");
    if (FEIGN_SECRET.equals(internalFeignHeader)) {
      filterChain.doFilter(request, response);
      return;
    }

    String email = request.getHeader("X-USER-EMAIL");
    String role = request.getHeader("X-USER-ROLE");

    if (email != null && role != null) {
      try {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        log.info("auth {}", authentication.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
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