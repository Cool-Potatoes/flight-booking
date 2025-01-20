package com.flight_booking.gateway_service.presentation.exception;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ErrorResponseUtil {

  private ErrorResponseUtil() {
    // 인스턴스 생성 방지
  }

  public static Mono<Void> createErrorResponse(ServerWebExchange exchange,
      JwtErrorCode jwtErrorCode) {

    HttpStatus status = HttpStatus.valueOf(jwtErrorCode.getStatusCode());
    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    // 메시지 JSON 형태로 생성
    String jsonResponse = String.format("{\"message\": \"%s\", \"httpStatus\": %d}",
        jwtErrorCode.getMessage(), status.value());
    DataBuffer buffer = exchange.getResponse().bufferFactory()
        .wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}