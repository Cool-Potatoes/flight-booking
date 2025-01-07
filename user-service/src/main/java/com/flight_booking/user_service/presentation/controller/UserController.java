package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.response.AdminUserDetailResponse;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserDetailResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;

  // 전체 회원 목록 조회
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ApiResponse<?> getAllUsers(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean isBlocked,
      Pageable pageable) {

    PageResponse<UserListResponse> response = userService.getUserList(email, name, role, isBlocked,
        pageable);

    return ApiResponse.ok(response, "회원 목록 조회 성공");
  }

  // 회원 상세 조회
  @GetMapping("/{id}")
  public ApiResponse<?> getUserById(
      @PathVariable(name = "id") Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    // 관리자일 경우
    if (userDetails.getAuthorities().equals("ADMIN")) {
      AdminUserDetailResponse response = userService.getAdminUserDetails(id);
      return ApiResponse.ok(response, "회원 상세 조회 성공");
    }

    // 사용자의 경우 본인 정보만 조회
    if (!userDetails.getId().equals(id)) {
      return ApiResponse.of(ErrorCode.ACCESS_ONLY_SELF.getHttpStatus(),
          Collections.singletonList(ErrorCode.ACCESS_ONLY_SELF.getMessage()));
    }

    // 사용자는 본인 정보만 제공
    UserDetailResponse response = userService.getUserDetails(id);
    return ApiResponse.ok(response, "회원 상세 조회 성공");
  }
}
