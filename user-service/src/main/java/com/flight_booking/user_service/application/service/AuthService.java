package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.infrastructure.security.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.LoginRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  // 회원가입
  public void createUser(SignUpRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserException(ErrorCode.DUPLICATE_EMAIL);
    }

    User user = User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password())) // 비밀번호 암호화 처리
        .name(request.name())
        .phone(request.phone())
        .role(request.role() != null ? request.role() : Role.USER)
        .isBlocked(false)
        .mileage(0L)
        .build();

    userRepository.save(user);
  }

  // 로그인
  public String authenticate(LoginRequest request) {
    // 이메일과 비밀번호를 사용한 인증 시도
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    // 인증 성공 후 SecurityContext에 인증 정보 설정
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 사용자 정보를 통해 JWT 생성
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return jwtUtil.createToken(userDetails.getUsername(), userDetails.getUser().getRole());
  }
}
