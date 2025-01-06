package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.presentation.global.ApiResponse;
import com.flight_booking.user_service.presentation.response.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;

  // 전체 회원 목록 조회
  @GetMapping
  public ApiResponse<?> getAllUsers(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role) {
    log.info("X-USER-EMAIL: {}", email);
    log.info("X-USER-ROLE: {}", role);
    List<User> users = userService.getAllUsers();

    List<UserResponse> userResponses = users.stream()
        .map(UserResponse::fromEntity)
        .toList();

    return ApiResponse.ok(userResponses, "사용자 목록 조회 성공");
  }
}
