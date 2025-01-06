package com.flight_booking.gateway_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j(topic = "JWT 검증 및 정보 추출")
@Component
public class JwtUtil {

  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // JWT 내 권한 키
  public static final String ROLE_KEY = "role";

  @Value("${SECRET_KEY}")
  private String secretKey;

  private SecretKey getSecretKey() {
    try {
      // Base64로 인코딩된 비밀키를 디코딩하여 SecretKey 생성
      byte[] decodedKey = Base64.getDecoder().decode(secretKey);
      return Keys.hmacShaKeyFor(decodedKey); // 디코딩된 키를 사용하여 SecretKey 생성
    } catch (IllegalArgumentException e) {
      log.error("SECRET_KEY 설정이 잘못되었습니다.", e);
      throw new RuntimeException(JwtErrorCode.INVALID_SECRET_KEY.getMessage(), e);
    }
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jws<Claims> claimsJws = Jwts.parser()
          .verifyWith(getSecretKey())
          .build().parseSignedClaims(token);
      log.info("payload: {}", claimsJws.getPayload().toString());
      return true;
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT token 입니다. Token: {}", token);
      throw new JwtException(JwtErrorCode.EXPIRED_TOKEN.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰 입니다. Token: {}", token);
      throw new JwtException(JwtErrorCode.UNSUPPORTED_TOKEN.getMessage());
    } catch (MalformedJwtException | IllegalArgumentException e) {
      log.error("잘못된 JWT 토큰 입니다. Token: {}", token);
      throw new JwtException(JwtErrorCode.MALFORMED_TOKEN.getMessage());
    } catch (SecurityException | JwtException e) {
      log.error("JWT 검증에 실패했습니다. Token: {}", token);
      throw new JwtException(JwtErrorCode.INVALID_SIGNATURE.getMessage());
    } catch (NullPointerException e) {
      log.error("토큰에 필요한 클레임 정보가 비어있습니다. Token: {}", token);
      throw new JwtException(JwtErrorCode.EMPTY_CLAIMS.getMessage());
    }
  }

  // 헤더에서 JWT 추출
  public String extractToken(ServerWebExchange exchange) {
    String bearerToken = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length()).trim();
    }
    return null;
  }

  // 이메일 추출
  public String extractEmail(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.getSubject();
    } catch (JwtException e) {
      log.error("이메일 추출에 실패했습니다. Token: {}", token, e);
      throw new JwtException(JwtErrorCode.EMAIL_EXTRACTION_FAILED.getMessage());
    }
  }

  // 역할 추출
  public String extractRole(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.get(ROLE_KEY, String.class);
    } catch (JwtException e) {
      log.error("역할 추출에 실패했습니다. Token: {}", token, e);
      throw new JwtException(JwtErrorCode.ROLE_EXTRACTION_FAILED.getMessage());
    }
  }

  // Claims 추출
  private Claims parseClaims(String token) {
    Jws<Claims> jws = Jwts.parser()
        .verifyWith(getSecretKey())
        .build().parseSignedClaims(token);
    return jws.getBody();
  }
}