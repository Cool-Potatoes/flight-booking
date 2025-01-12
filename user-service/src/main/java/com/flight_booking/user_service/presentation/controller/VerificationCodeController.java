package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.VerificationCodeService;
import com.flight_booking.user_service.presentation.request.SendCodeRequest;
import com.flight_booking.user_service.presentation.request.VerificationCodeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class VerificationCodeController {

  private final VerificationCodeService verificationCodeService;

  // 비밀번호 찾기: 인증번호 발급
  @PostMapping("/send-code")
  public ApiResponse<?> sendVerificationCode(@Valid @RequestBody SendCodeRequest request) {
    verificationCodeService.sendVerificationCode(request);
    return ApiResponse.ok("인증 코드가 이메일로 전송되었습니다.");
  }

  // 비밀번호 찾기: 인증번호 검증
  @PostMapping("/verify-code")
  public ApiResponse<?> verifyCode(@Valid @RequestBody VerificationCodeRequest request) {
    String tmpToken = verificationCodeService.verifyCode(request);
    return ApiResponse.ok("인증 코드가 유효합니다.", tmpToken);
  }

}