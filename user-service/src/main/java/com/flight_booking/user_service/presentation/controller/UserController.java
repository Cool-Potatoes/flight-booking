package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import com.flight_booking.user_service.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;

  // 이메일 기반으로 사용자 정보 조회
  @GetMapping("/{email}")
  public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
    UserResponse user = userService.findUserByEmail(email);
    return ResponseEntity.ok(user);
  }

  // 전체 회원 목록 조회
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ApiResponse<?> getAllUsers(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean isBlocked,
      Pageable pageable) {

    PageResponse<UserListResponse> response = userService.getUserList(
        email, name, role, isBlocked, pageable);

    return ApiResponse.ok(response, "회원 목록 조회 성공");
  }

  // 회원 상세 조회
  @GetMapping("/{id}")
  public ApiResponse<?> getUserDetails(
      @PathVariable(name = "id") Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    // 사용자는 본인 정보만 제공
    Object response = userService.getUserDetails(id, userDetails);
    return ApiResponse.ok(response, "회원 상세 조회 성공");
  }

  // 회원 정보 수정
  @PatchMapping("/{id}")
  public ApiResponse<?> updateUser(
      @PathVariable(name = "id") Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody UpdateRequest updateRequest) {

    userService.updateUser(id, userDetails, updateRequest);
    return ApiResponse.ok("회원 정보 수정 성공");
  }

  // 회원 탈퇴
  @PreAuthorize("hasRole('ROLE_USER')")
  @DeleteMapping("/{id}")
  public ApiResponse<?> deleteUser(
      @PathVariable(name = "id") Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    userService.deleteUser(id, userDetails);
    return ApiResponse.ok("회원 탈퇴 성공");
  }
}
