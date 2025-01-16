package com.flight_booking.gateway_service.filter;

import com.flight_booking.gateway_service.util.JwtUtil;
import java.util.List;
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
    // 인증이 필요 없는 경로 리스트 정의
    List<String> excludedPaths = List.of(
        "/v1/auth/signup",
        "/v1/auth/signin",
        "/v1/auth/find-id",
        "/v1/auth/send-code",
        "/v1/auth/verify-code",
        "/v1/auth/token"
    );

    // 경로가 제외 리스트에 포함되어 있으면 인증 없이 필터 통과
    String path = exchange.getRequest().getURI().getPath();
    if (excludedPaths.contains(path)) {
      return chain.filter(exchange);
    }

    // Authorization 헤더에서 JWT 토큰 추출
    String token = jwtUtil.extractToken(exchange);

    if (token == null) {
      log.info("토큰이 존재하지 않습니다.");
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    try {
      jwtUtil.validateToken(token);
      log.info("토큰 검증 완료");

      jwtUtil.isTokenBlacklisted(token);
      log.info("블랙리스트 체크 완료");

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
    } catch (IllegalArgumentException e) {
      log.warn("유효하지 않은 토큰: {}", e.getMessage());
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }
}