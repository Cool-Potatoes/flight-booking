package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.infrastructure.security.jwt.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.FindIdRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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
      String role = userDetails.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .findFirst()
          .orElse(Role.USER.getAuthority());

      // 사용자 상태 확인 (블락/ 탈퇴)
      checkUserStatus(validatedEmail);

      return jwtUtil.createToken(validatedEmail, role);
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
}