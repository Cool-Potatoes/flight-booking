package com.flight_booking.user_service.infrastructure.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.infrastructure.security.JwtUtil;
import com.flight_booking.user_service.presentation.global.ApiResponse;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.request.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;

  public AuthenticationFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
    this.jwtUtil = jwtUtil;
    this.setAuthenticationManager(authenticationManager);
    setFilterProcessesUrl("/v1/auth/login"); // 로그인 요청 URL 설정
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
      log.info("로그인 시도");
      // 요청 본문에서 로그인 정보 (이메일, 비밀번호) 추출
      LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(),
          LoginRequest.class);

      // 이메일 확인 로그 추가
      log.info("로그인 요청 이메일: {}", loginRequest.email());

      // 인증 시도
      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.email(), loginRequest.password()
          )
      );
    } catch (IOException e) {
      throw new RuntimeException("로그인 요청 처리 중 오류 발생", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    log.info("로그인 성공 및 JWT 생성");
    // 인증 성공 후 JWT 생성
    String email = ((CustomUserDetails) authResult.getPrincipal()).getUsername();
    Role role = ((CustomUserDetails) authResult.getPrincipal()).getUser().getRole();
    String token = jwtUtil.createToken(email, role);

    // JWT 토큰을 응답 헤더에 추가
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

    // 로그인 성공 응답
    ApiResponse<?> apiResponse = ApiResponse.ok("로그인 성공", token);
    response.setContentType("application/json;charset=UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), apiResponse);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    log.error("로그인 실패: {}", failed.getMessage());
    // 인증 실패 시 응답 처리
    ApiResponse<?> errorResponse = ApiResponse.builder()
        .message(ErrorCode.LOGIN_FAIL.getMessage())
        .httpStatus(HttpStatus.UNAUTHORIZED)
        .build();

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
  }
}