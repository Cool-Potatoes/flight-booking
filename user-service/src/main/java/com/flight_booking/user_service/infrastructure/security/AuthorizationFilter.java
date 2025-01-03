package com.flight_booking.user_service.infrastructure.security;

import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String token = jwtUtil.getJwtFromHeader(request);

    if (token != null && jwtUtil.validateToken(token)) {
      // 유효한 토큰일 경우, 이메일과 권한 정보 추출
      String email = jwtUtil.validateTokenAndExtractUsername(token);

      // 이메일을 통해 사용자 정보를 로드
      var userDetails = customUserDetailsService.loadUserByUsername(email);

      // 사용자 인증 객체 생성 (권한 포함)
      var authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());

      // 인증 정보를 SecurityContext에 설정
      SecurityContextHolder.getContext()
          .setAuthentication(authentication); // 인증 정보를 SecurityContext에 설정
    }

    // 필터 체인의 다음 필터로 요청을 전달
    filterChain.doFilter(request, response);
  }
}