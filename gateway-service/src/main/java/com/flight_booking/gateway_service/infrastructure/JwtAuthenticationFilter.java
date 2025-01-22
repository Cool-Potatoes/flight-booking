package com.flight_booking.gateway_service.infrastructure;

import com.flight_booking.gateway_service.application.UserFeignService;
import com.flight_booking.gateway_service.application.UserStatusDto;
import com.flight_booking.gateway_service.presentation.exception.ErrorResponseUtil;
import com.flight_booking.gateway_service.presentation.exception.JwtErrorCode;
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
  private final UserFeignService userFeignService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 인증이 필요 없는 경로 리스트
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
    if (excludedPaths.contains(path) || path.startsWith("/v1/users/status/")) {
      return chain.filter(exchange);
    }

    String token = jwtUtil.extractToken(exchange);

    if (token == null) {
      log.info("토큰이 존재하지 않습니다.");
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    try {
      // JWT 검증
      jwtUtil.validateToken(token);

      // 블랙리스트 체크
      if (jwtUtil.isTokenBlacklisted(token)) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.BLACKLISTED_TOKEN);
      }

      // 토큰이 유효한 경우, 이메일과 역할 추출
      String email = jwtUtil.extractEmail(token);

      // 사용자 상태(블락/탈퇴) 확인
      UserStatusDto userStatusDto = userFeignService.getUserStatus(email);

      if (userStatusDto.isBlocked()) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.USER_BLOCKED);
      }

      if (userStatusDto.isDeleted()) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.USER_DELETED);
      }

      String role = jwtUtil.extractRole(token);

      // 이메일과 역할을 헤더에 추가
      ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
          .header("X-USER-EMAIL", email)
          .header("X-USER-ROLE", role)
          .build();

      exchange = exchange.mutate().request(modifiedRequest).build();

      return chain.filter(exchange);
    } catch (Exception e) {
      log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
      return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.TOKEN_VALIDATION_ERROR);
    }
  }
}