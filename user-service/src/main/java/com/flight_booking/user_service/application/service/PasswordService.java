package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.ChangePwRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // 로그인 후 비밀번호 변경
  @Transactional
  public void changePw(String email, ChangePwRequest request) {
    User user = getUser(email);

    // 기존 비밀번호 검증
    if (!passwordEncoder.matches(request.currentPw(), user.getPassword())) {
      throw new UserException(ErrorCode.INVALID_CURRENT_PASSWORD);
    }

    // 기존 비밀번호와 새로운 비밀번호가 같은지 확인
    if (passwordEncoder.matches(request.newPw(), user.getPassword())) {
      throw new UserException(ErrorCode.PASSWORDS_SAME);
    }

    // 새로운 비밀번호와 확인 비밀번호가 일치하는지 확인
    if (!request.newPw().equals(request.confirmPw())) {
      throw new UserException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
    }

    user.setPassword(passwordEncoder.encode(request.newPw()));
  }

  // 이메일로 사용자 찾기
  private User getUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }

}