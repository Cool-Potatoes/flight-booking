package com.flight_booking.user_service.infrastructure.security;

import com.flight_booking.user_service.domain.model.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // 사용자 권한 값의 KEY
  public static final String AUTHORIZATION_KEY = "auth";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // 토큰 만료시간
  private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

  @Value("${SECRET_KEY}")
  private byte[] secretKey;
  private SecretKey key;

  @PostConstruct
  public void init() {
    key = Keys.hmacShaKeyFor(secretKey);
  }

  // 토큰 생성
  public String createToken(String email, Role role) {
    Date now = new Date(System.currentTimeMillis());
    Date expiration = new Date(now.getTime() + TOKEN_TIME);

    return BEARER_PREFIX + Jwts.builder()
        .subject(email) //주체
        .claim(AUTHORIZATION_KEY, role.name()) // 사용자 권한
        .issuedAt(now) // 발급 시간
        .expiration(expiration) // 만료 시간
        .signWith(key)
        .compact();
  }

  public String validateTokenAndExtractUsername(String token) {
    if (!validateToken(token)) {
      return null;
    }
    return extractUsername(token);
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (MalformedJwtException e) {
      log.error("Malformed JWT token, 잘못된 JWT 토큰 입니다.");
    } catch (SecurityException | JwtException e) {
      log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  // 헤더에서 JWT 추출
  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length()).trim();
    }
    return null;
  }

  // 토큰에서 사용자 이메일 추출
  public String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getBody()
        .getSubject();  // 이메일(주체)
  }
}