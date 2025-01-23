package com.flight_booking.gateway_service.infrastructure;

import com.flight_booking.gateway_service.application.UserCacheService;
import com.flight_booking.gateway_service.application.UserInfo;
import com.flight_booking.gateway_service.presentation.exception.CustomJwtException;
import com.flight_booking.gateway_service.presentation.exception.ErrorResponseUtil;
import com.flight_booking.gateway_service.presentation.exception.JwtErrorCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JWT 인증 처리")
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

  private final UserCacheService userCacheService;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(@Lazy UserCacheService userCacheService, JwtUtil jwtUtil) {
    this.userCacheService = userCacheService;
    this.jwtUtil = jwtUtil;
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
      return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.TOKEN_NOT_FOUND);
    }

    try {
      // JWT 검증
      jwtUtil.validateToken(token);

      // 블랙리스트 체크
      if (jwtUtil.isTokenBlacklisted(token)) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.BLACKLISTED_TOKEN);
      }

      // 사용자 상태(블락/탈퇴) 확인
      UserInfo userInfo = userCacheService.getUserInfo(token);

      if (userInfo.isBlocked()) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.USER_BLOCKED);
      }

      if (userInfo.isDeleted()) {
        return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.USER_DELETED);
      }

      // 이메일과 역할을 헤더에 추가
      ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
          .header("X-USER-EMAIL", userInfo.email())
          .header("X-USER-ROLE", userInfo.role())
          .build();

      exchange = exchange.mutate().request(modifiedRequest).build();
    } catch (CustomJwtException e) {
      return ErrorResponseUtil.createErrorResponse(exchange, e.getErrorCode());
    } catch (Exception e) {
      log.error("알 수 없는 오류: {}", e.getMessage());
      return ErrorResponseUtil.createErrorResponse(exchange, JwtErrorCode.TOKEN_VALIDATION_ERROR);
    }
    return chain.filter(exchange);
  }
}