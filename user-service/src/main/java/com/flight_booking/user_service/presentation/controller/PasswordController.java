package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.PasswordService;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.ChangePwRequest;
import com.flight_booking.user_service.presentation.request.ResetPwRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class PasswordController {

  private final PasswordService passwordService;

  // 로그인 후 비밀번호 변경
  @PatchMapping("/change-pw")
  public ApiResponse<?> changePw(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody ChangePwRequest request) {
    String email = userDetails.getUsername();
    passwordService.changePw(email, request);
    return ApiResponse.ok("비밀번호가 변경되었습니다.");
  }

  // 비밀번호 찾기: 비밀번호 변경
  @PatchMapping("/reset-pw")
  public ApiResponse<?> resetPw(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody ResetPwRequest request) {
    String email = userDetails.getUsername();
    passwordService.resetPw(email, request);
    return ApiResponse.ok("비밀번호가 재설정되었습니다.");
  }

}