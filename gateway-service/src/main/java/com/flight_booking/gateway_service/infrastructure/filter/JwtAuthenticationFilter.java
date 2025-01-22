package com.flight_booking.gateway_service.infrastructure.filter;

import com.flight_booking.gateway_service.UserFeignClient;
import com.flight_booking.gateway_service.application.UserStatusDto;
import com.flight_booking.gateway_service.infrastructure.JwtUtil;
import com.flight_booking.gateway_service.presentation.exception.ErrorResponseUtil;
import com.flight_booking.gateway_service.presentation.exception.JwtErrorCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JWT 인증 처리")
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

  private final JwtUtil jwtUtil;
  private final UserFeignClient userFeignClient;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, @Lazy UserFeignClient userFeignClient) {
    this.jwtUtil = jwtUtil;
    this.userFeignClient = userFeignClient;
  }

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
      // 토큰 검증
      jwtUtil.validateToken(token);
      log.info("토큰 검증 완료");

      // 블랙리스트 체크
      if (jwtUtil.isTokenBlacklisted(token)) {
        log.info("토큰이 블랙리스트에 존재합니다.");
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.BLACKLISTED_TOKEN);
      }
      log.info("블랙리스트 체크 완료");

      // 토큰이 유효한 경우, 이메일과 역할 추출
      String email = jwtUtil.extractEmail(token);

      UserStatusDto userStatusDto = userFeignClient.getUserStatus(email);

      if (userStatusDto.isBlocked()) {
        log.info("블락 처리된 회원입니다.");
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.USER_BLOCKED);
      }

      if (userStatusDto.isDeleted()) {
        log.info("탈퇴한 회원입니다.");
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
    } catch (IllegalArgumentException e) {
      log.warn("유효하지 않은 토큰: {}", e.getMessage());
      return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.INVALID_TOKEN);
    } catch (Exception e) {
      log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
      return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.TOKEN_VALIDATION_ERROR);
    }
  }
}