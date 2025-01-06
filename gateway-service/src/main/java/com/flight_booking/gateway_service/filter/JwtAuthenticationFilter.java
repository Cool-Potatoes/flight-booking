package com.flight_booking.gateway_service.filter;

import com.flight_booking.gateway_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JWT 인증 처리")
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

  private final JwtUtil jwtUtil;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 로그인 및 회원가입 경로는 인증 없이 처리
    String path = exchange.getRequest().getURI().getPath();
    if (path.equals("/v1/auth/signup") || path.equals("/v1/auth/signin")) {
      return chain.filter(exchange); // 인증 없이 다음 필터로 넘김
    }

    // Authorization 헤더에서 JWT 토큰 추출
    String token = jwtUtil.extractToken(exchange);

    if (token == null || !jwtUtil.validateToken(token)) {
      log.info("토큰 검증 실패");
      // 유효하지 않은 토큰이면 UNAUTHORIZED 응답 반환
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    // 토큰이 유효한 경우, 이메일과 역할 추출
    String email = jwtUtil.extractEmail(token);
    String role = jwtUtil.extractRole(token);

    // 이메일과 역할을 헤더에 추가
    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
        .header("X-USER-EMAIL", email)
        .header("X-USER-ROLE", role)
        .build();

    exchange = exchange.mutate().request(modifiedRequest).build();

    return chain.filter(exchange);
  }
}