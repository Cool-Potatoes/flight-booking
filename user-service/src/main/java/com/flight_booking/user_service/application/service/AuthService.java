package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.jwt.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.FindIdRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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

  // 회원가입
  @Transactional
  public void createUser(SignUpRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserException(ErrorCode.DUPLICATE_EMAIL);
    }

    if (userRepository.existsByPhone(request.phone())) {
      throw new UserException(ErrorCode.DUPLICATE_PHONE);
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
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

      User user = getUser(email);
      validateUserStatus(user);
      String role = user.getRole().toString();

      String accessToken = jwtUtil.createAccessToken(email, role);
      String refreshToken = jwtUtil.createRefreshToken(email);

      jwtUtil.addRefreshTokenToCookie(refreshToken, response);

      return accessToken;
    } catch (AuthenticationException ex) {
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

    validateUserStatus(user);

    return user.getEmail();
  }

  // 토큰 재발급
  public String renewTokens(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = jwtUtil.extractAccessTokenFromRequest(request);
    String refreshToken = jwtUtil.extractRefreshTokenFromRequest(request);

    // refreshToken 확인 및 검증
    validateRefreshToken(refreshToken);

    // 기존 Access Token 처리
    String accessTokenWithoutBearer = jwtUtil.removeBearer(accessToken);
    if (jwtUtil.isTokenBlacklisted(accessTokenWithoutBearer)) {
      throw new UserException(ErrorCode.BLACKLISTED_TOKEN);
    }
    jwtUtil.addToBlacklist(accessTokenWithoutBearer); // 만료X 시 블랙리스트에 추가

    // 사용자 정보 추출
    String email = jwtUtil.getEmail(refreshToken);
    User user = getUser(email);
    validateUserStatus(user);
    String role = user.getRole().toString();

    // 새로운 Access Token, Refresh Token 발급
    String newAccessToken = jwtUtil.createAccessToken(email, role);
    String newRefreshToken = jwtUtil.createRefreshToken(email);

    jwtUtil.addToBlacklist(refreshToken); // 기존 Refresh Token 블랙리스트 추가
    jwtUtil.addRefreshTokenToCookie(newRefreshToken, response); // HTTP-Only 쿠키 생성

    return newAccessToken;
  }

  // 로그아웃
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = jwtUtil.extractAccessTokenFromRequest(request);
    String refreshToken = jwtUtil.extractRefreshTokenFromRequest(request);

    jwtUtil.addToBlacklist(accessToken);
    jwtUtil.addToBlacklist(refreshToken);

    jwtUtil.deleteRefreshTokenFromCookie(refreshToken, response);
    SecurityContextHolder.clearContext();
  }

  // private method ------------------------------------------------------------------------------------

  // 이메일로 사용자 확인
  private User getUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }

  // 사용자 상태 확인 (블락/ 탈퇴)
  private void validateUserStatus(User user) {
    if (user.getIsBlocked()) {
      String reason = user.getBlockedInfo().getBlockedReason()
          .get(user.getBlockedInfo().getBlockedReason().size() - 1);
      log.error("블락된 사용자: {} (이유: {})", user.getEmail(), reason);
      throw new UserException(ErrorCode.USER_BLOCKED, "사유: " + reason);
    }

    if (user.getIsDeleted()) {
      log.error("탈퇴된 사용자: {}", user.getEmail());
      throw new UserException(ErrorCode.USER_DELETED);
    }
  }

  // refreshToken 검증
  private void validateRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new UserException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
    if (!jwtUtil.validateToken(refreshToken)) {
      throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    if (jwtUtil.isTokenBlacklisted(refreshToken)) {
      throw new UserException(ErrorCode.BLACKLISTED_TOKEN);
    }
  }
}