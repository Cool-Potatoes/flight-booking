package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.AuthService;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.ChangePwRequest;
import com.flight_booking.user_service.presentation.request.FindIdRequest;
import com.flight_booking.user_service.presentation.request.SignInRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

  private final AuthService authService;

  // 회원가입
  @PostMapping("/signup")
  public ApiResponse<?> signUp(@Valid @RequestBody SignUpRequest request) {
    authService.createUser(request);
    return ApiResponse.ok("회원가입 성공");
  }

  // 로그인
  @PostMapping("/signin")
  public ApiResponse<?> signIn(@Valid @RequestBody SignInRequest request) {
    log.info("로그인 시도");
    String token = authService.signIn(request.email(), request.password());
    return ApiResponse.ok("로그인 성공", token);
  }

  // 아이디 찾기
  @PostMapping("/find-id")
  public ApiResponse<?> findId(@Valid @RequestBody FindIdRequest request) {
    String email = authService.findId(request);
    return ApiResponse.ok("아이디 찾기 성공", email);
  }

  // 비밀번호 변경
  @PostMapping("/change-pw")
  public ApiResponse<?> changePw(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody ChangePwRequest request) {
    String email = userDetails.getUsername();
    authService.changePw(email, request);
    return ApiResponse.ok("비밀번호가 변경되었습니다.");
  }

}
