package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.infrastructure.security.jwt.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.FindIdRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

  @Value("${service.jwt.access-expiration}")
  private long TOKEN_EXPIRATION;

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
  public String signIn(String email, String password) {
    try {
      log.info("service");
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, password)
      );
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      String validatedEmail = userDetails.getUsername();
      String role = userDetails.getUser().getRole().toString();

      // 사용자 상태 확인 (블락/ 탈퇴)
      checkUserStatus(validatedEmail);

      // JWT 생성
      String token = jwtUtil.createToken(validatedEmail, role);

      // JWT를 Redis에 저장 (토큰 저장은 JwtUtil에서 처리하는 방식으로도 가능)
      redisTemplate.opsForValue()
          .set(token, validatedEmail, TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);

      return token;
    } catch (AuthenticationException ex) {
      // 인증 실패 시 처리
      log.error("로그인 실패: {}", ex.getMessage());
      throw new UserException(ErrorCode.LOGIN_FAIL);
    }
  }

  // 사용자 상태 확인 (블락/ 탈퇴)
  private void checkUserStatus(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

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

  // 아이디 찾기
  @Transactional(readOnly = true)
  public String findId(FindIdRequest request) {
    String name = request.name();
    String phone = request.phone();
    User user = userRepository.findByNameAndPhone(name, phone)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    return user.getEmail();
  }

  // 로그아웃
  @Transactional
  public void logout(String token) {
    // "Bearer " 접두어 제거
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);  // "Bearer " 부분 제거
    }

    // Redis에서 토큰이 존재하는지 확인하고 삭제
    if (redisTemplate.opsForValue().get(token) != null) {
      log.info("토큰 존재함: {}", token);
      redisTemplate.delete(token);
    }
  }
}