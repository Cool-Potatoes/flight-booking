package com.flight_booking.gateway_service.filter;

import com.flight_booking.gateway_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JWT 인증")
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

  private final JwtUtil jwtUtil;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 로그인 및 회원가입 경로는 인증 없이 처리
    String path = exchange.getRequest().getURI().getPath();
    if (path.startsWith("/v1/auth/signUp") || path.startsWith("/v1/auth/login")) {
      return chain.filter(exchange); // 인증 없이 다음 필터로 넘김
    }

    // Authorization 헤더에서 JWT 토큰 추출
    String token = jwtUtil.extractToken(exchange);

    if (token == null || !jwtUtil.validateToken(token)) {
      // 토큰이 없거나 유효하지 않으면 UNAUTHORIZED 응답 반환
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    // 유효한 토큰일 경우 다음 필터로 요청을 전달
    return chain.filter(exchange);
  }
}