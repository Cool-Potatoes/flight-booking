package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

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
}
