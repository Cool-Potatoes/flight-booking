package com.flight_booking.gateway_service.filter;

import java.util.logging.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomPostFilter implements GlobalFilter, Ordered {

  private static final Logger log = Logger.getLogger(CustomPostFilter.class.getName());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
      ServerHttpResponse response = exchange.getResponse();
      log.info("Response status code: " + response.getStatusCode());
    }));
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
