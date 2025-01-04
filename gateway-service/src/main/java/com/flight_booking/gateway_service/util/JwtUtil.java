package com.flight_booking.gateway_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";

  @Value("${SECRET_KEY}")
  private String secretKey;

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
      Jws<Claims> claimsJws = Jwts.parser()
          .verifyWith(key)
          .build().parseSignedClaims(token);
      log.info("payload: {}", claimsJws.getPayload().toString());
      return true;
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰 입니다.");
    } catch (MalformedJwtException | IllegalArgumentException e) {
      log.error("잘못된 JWT 토큰 입니다.");
    } catch (SecurityException | JwtException e) {
      log.error("유효하지 않는 JWT 서명 입니다.");
    }
    return false;
  }

  // 헤더에서 JWT 추출
  public String extractToken(ServerWebExchange exchange) {
    String bearerToken = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length()).trim();
    }
    return null;
  }
}