package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.user_service.application.service.AuthService;
import com.flight_booking.user_service.presentation.global.ApiResponse;
import com.flight_booking.user_service.presentation.request.SignInRequest;
import com.flight_booking.user_service.presentation.request.SignUpRequest;
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
}
