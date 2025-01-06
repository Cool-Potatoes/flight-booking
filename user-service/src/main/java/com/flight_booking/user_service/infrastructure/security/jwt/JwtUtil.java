package com.flight_booking.user_service.infrastructure.security.jwt;

import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Jwt 토큰 생성")
@Component
public class JwtUtil {

  // HTTP Authorization 헤더 키
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // JWT 내 권한 키
  public static final String JWT_ROLE_KEY = "role";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // 서비스 이름 (JWT 발행자)
  @Value("${spring.application.name}")
  private String issuer;
  // 토큰 만료 시간
  @Value("${service.jwt.access-expiration}")
  private long TOKEN_EXPIRATION;

  @Value("${SECRET_KEY}")
  private String secretKey; // Base64 인코딩된 비밀키
  private SecretKey key; // 디코딩된 비밀키 객체

  @PostConstruct
  public void init() {
    try {
      byte[] decodedKey = Base64.getDecoder().decode(secretKey);
      key = Keys.hmacShaKeyFor(decodedKey); // HMAC-SHA 키 생성
    } catch (IllegalArgumentException e) {
      log.error("SECRET_KEY 설정이 잘못되었습니다.", e);
      throw new RuntimeException(ErrorCode.INVALID_SECRET_KEY.getMessage(), e);
    }
  }

  // 토큰 생성
  public String createToken(String email, String role) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + TOKEN_EXPIRATION);

    return BEARER_PREFIX + Jwts.builder()
        .subject(email) // 발행자
        .claim(JWT_ROLE_KEY, role) // 사용자 권한
        .issuer(issuer)
        .issuedAt(now) // 발급 시간
        .expiration(expiration) // 만료 시간
        .signWith(key, SIG.HS512)
        .compact();
  }
}