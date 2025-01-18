package com.flight_booking.user_service.infrastructure.security.jwt;

import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Jwt 토큰 생성")
@Component
public class JwtUtil {

  public static final String REFRESH_TOKEN_COOKIE = "RefreshToken";  // Refresh Token 쿠키 이름
  public static final String JWT_ROLE_KEY = "role";  // JWT 내 권한 키
  public static final String BEARER_PREFIX = "Bearer "; // Token 식별자

  @Value("${spring.application.name}")
  private String issuer;  // 서비스 이름 (JWT 발행자)

  @Value("${service.jwt.access-token-expiration}")
  private long accessTokenExpiration; // 토큰 만료 시간 (Access Token)

  @Value("${service.jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;  // 토큰 만료 시간 (Refresh Token)

  @Value("${SECRET_KEY}")
  private String secretKey; // Base64 인코딩된 비밀키
  private SecretKey key; // 디코딩된 비밀키 객체

  private final RedisTemplate<String, Object> redisTemplate;

  public JwtUtil(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

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

  // AccessToken 생성
  public String createAccessToken(String email, String role) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + accessTokenExpiration);

    return BEARER_PREFIX + Jwts.builder()
        .subject(email)
        .claim(JWT_ROLE_KEY, role)
        .issuer(issuer)
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(key, SIG.HS256)
        .compact();
  }

  // RefreshToken 생성
  public String createRefreshToken(String email) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .subject(email)
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(key, SIG.HS256)
        .compact();
  }

  // Claims 추출
  private Claims getClaims(String token) {
    Jws<Claims> jws = Jwts.parser()
        .verifyWith(key)
        .build().parseSignedClaims(token);
    return jws.getPayload();
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("토큰이 만료되었습니다.");
    } catch (JwtException | IllegalArgumentException e) {
      log.error("유효하지 않은 토큰입니다.");
    }
    return false;
  }

  // "Bearer " 및 공백 제거
  public String removeBearer(String token) {
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return token.trim();
  }

  // 이메일 추출
  public String getEmail(String token) {
    return getClaims(token).getSubject();
  }

  // 만료 시간 추출
  public Date getExpiration(String token) {
    return getClaims(token).getExpiration();
  }

  // 토큰의 남은 시간 계산
  public long calculateRemainingTime(String token) {
    Date expiration = getExpiration(token);
    long remainingTime = expiration.getTime() - System.currentTimeMillis();
    return Math.max(remainingTime, 0);
  }

  // 토큰이 블랙리스트에 있는지 확인
  public boolean isTokenBlacklisted(String token) {
    return redisTemplate.hasKey("blacklist:" + token);
  }

  // 만료된 토큰이 아닌 경우에만 블랙리스트에 추가
  public void addToBlacklist(String token) {
    long remainingTime = calculateRemainingTime(token);
    if (remainingTime <= 0) {
      log.warn("만료된 토큰입니다. 토큰: {}", token);
      return;
    }
    // 남은 시간이 0보다 클 때만 블랙리스트에 추가
    redisTemplate.opsForValue().set("blacklist:" + token, "true", Duration.ofMillis(remainingTime));
    log.info("블랙리스트에 토큰이 추가되었습니다. 토큰: {}", token);
  }

  // RefreshToken을 쿠키에 저장
  public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
    log.info("쿠키 설정 값: {}", refreshToken);
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
    cookie.setHttpOnly(true);   // 클라이언트에서 접근 불가
//      cookie.setSecure(true);     // HTTPS에서만 전송 (현재 HTTP)
    cookie.setPath("/");        // 쿠키 경로
    cookie.setMaxAge(86400);    // 만료 시간 (1일)
    response.addCookie(cookie);
    log.info("Refresh token 쿠키가 성공적으로 설정되었습니다.");
  }

  // RefreshToken을 쿠키에서 삭제
  public void deleteRefreshTokenFromCookie(String refreshToken, HttpServletResponse response) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
    cookie.setHttpOnly(true);
//    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);  // 쿠키 만료
    response.addCookie(cookie);
    log.info("Refresh token 쿠키가 성공적으로 삭제되었습니다.");
  }
}