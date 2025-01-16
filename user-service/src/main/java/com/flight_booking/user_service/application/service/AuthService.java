package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetailsService;
import com.flight_booking.user_service.infrastructure.security.jwt.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.FindIdRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, Object> redisTemplate;
  private final CustomUserDetailsService customUserDetailsService;

  // 회원가입
  @Transactional
  public void createUser(SignUpRequest request) {
    if (userRepository.existsByEmail(request.email()) && userRepository.existsByPhone(
        request.phone())) {
      throw new UserException(ErrorCode.DUPLICATE_EMAIL);
    }

    User user = User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password())) // 비밀번호 암호화 처리
        .name(request.name())
        .phone(request.phone())
        .isBlocked(false)
        .mileage(0L)
        .build();

    userRepository.save(user);
  }

  // 로그인
  public String signIn(String email, String password, HttpServletResponse response) {
    try {
      // 이메일로 사용자 조회
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

      // 사용자 상태 확인 (블락/ 탈퇴)
      validateUserStatus(user);

      // 인증 처리
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, password)
      );

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      String role = userDetails.getUser().getRole().toString();

      // 토큰 생성
      String accessToken = jwtUtil.createAccessToken(email, role);
      String refreshToken = jwtUtil.createRefreshToken(email);

      addRefreshTokenToCookie(refreshToken, response);

      return accessToken;
    } catch (AuthenticationException ex) {
      // 인증 실패 시 처리
      log.error("로그인 실패: {}", ex.getMessage());
      throw new UserException(ErrorCode.LOGIN_FAIL);
    }
  }

  // 아이디 찾기
  @Transactional(readOnly = true)
  public String findId(FindIdRequest request) {
    String name = request.name();
    String phone = request.phone();
    User user = userRepository.findByNameAndPhone(name, phone)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    // 사용자 상태 확인
    validateUserStatus(user);

    return user.getEmail();
  }

  // 토큰 재발급
  public String refreshAccessToken(String refreshToken, String accessToken,
      HttpServletResponse response) {

    // 리프레시 토큰이 없는 경우
    if (refreshToken.isEmpty()) {
      throw new UserException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    // Refresh Token 검증
    if (!jwtUtil.validateToken(refreshToken)) {
      throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 블랙리스트에 있는 리프레시 토큰인지 확인
    if (isTokenBlacklisted(refreshToken)) {
      throw new UserException(ErrorCode.BLACKLISTED_TOKEN);
    }

    // 기존 Access Token 처리
    String token = jwtUtil.removeBearer(accessToken);
    if (isTokenBlacklisted(token)) {
      throw new UserException(ErrorCode.BLACKLISTED_TOKEN);
    }
    addToBlacklist(token); // 만료X 시 추가

    // 사용자 정보 추출
    String email = jwtUtil.getEmail(refreshToken);
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
    String role = userDetails.getAuthorities().toString();

    // 새로운 Access Token, Refresh Token 발급
    String newAccessToken = jwtUtil.createAccessToken(email, role);
    String newRefreshToken = jwtUtil.createRefreshToken(email);

    // 기존 Refresh Token 블랙리스트 추가
    addToBlacklist(refreshToken);

    // HTTP-Only 쿠키 생성
    addRefreshTokenToCookie(newRefreshToken, response);

    return newAccessToken;
  }

  // 로그아웃
  public void logout(String token, HttpServletResponse response) {
    if (isTokenBlacklisted(token)) {
      throw new UserException(ErrorCode.BLACKLISTED_TOKEN);
    }

    addToBlacklist(token);

    // 쿠키 삭제
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(0);  // 쿠키 만료
    response.addCookie(cookie);
  }

  // ------------------------------------------------------------------------------------

  // 사용자 상태 확인 (블락/ 탈퇴)
  private void validateUserStatus(User user) {
    String email = user.getEmail();
    if (user.getIsBlocked()) {
      log.error("블락된 사용자: {}", email);
      List<String> reasons = user.getBlockedInfo().getBlockedReason();
      String reason = reasons.get(reasons.size() - 1);
      String errorMessage = ErrorCode.USER_BLOCKED.getMessage() + " 이유: " + reason;
      throw new UserException(ErrorCode.USER_BLOCKED, errorMessage);
    }

    if (user.getIsDeleted()) {
      log.error("탈퇴된 사용자: {}", email);
      throw new UserException(ErrorCode.USER_DELETED);
    }
  }

  // RefreshToken을 쿠키에 저장
  private void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
    log.info("쿠키 설정 값: {}", refreshToken);
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);   // 클라이언트에서 접근 불가
//      cookie.setSecure(true);     // HTTPS에서만 전송 (현재 HTTP)
    cookie.setPath("/");        // 쿠키 경로
    cookie.setMaxAge(86400);    // 만료 시간 (1일)
    response.addCookie(cookie);
    log.info("Refresh token 쿠키가 성공적으로 설정되었습니다.");
  }

  // 토큰이 블랙리스트에 있는지 확인
  private boolean isTokenBlacklisted(String token) {
    return redisTemplate.hasKey("blacklist:" + token);
  }

  // 만료된 토큰이 아닌 경우에만 블랙리스트에 추가
  private void addToBlacklist(String token) {
    long remainingTime = jwtUtil.calculateRemainingTime(token);
    if (remainingTime > 0) {
      redisTemplate.opsForValue()
          .set("blacklist:" + token, "true", Duration.ofMillis(remainingTime));
      log.info("블랙리스트에 토큰이 추가되었습니다. 토큰: {}", token);
    } else {
      log.warn("만료된 토큰입니다. 토큰: {}", token);
      throw new UserException(ErrorCode.TOKEN_EXPIRED);
    }
  }
}