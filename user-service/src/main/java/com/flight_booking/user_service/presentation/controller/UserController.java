package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.dto.UserStatusDto;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.DeleteRequest;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Slf4j
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

    PageResponse<UserListResponse> response = userService.getUserList(
        email, name, role, isBlocked, pageable);
    return ApiResponse.ok(response, "회원 목록 조회 성공");
  }

  // 회원 상세 조회
  @GetMapping("/{id}")
  public ApiResponse<?> getUserDetails(
      @PathVariable Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Object response = userService.getUserDetails(id, userDetails);
    return ApiResponse.ok(response, "회원 상세 조회 성공");
  }

  // 회원 정보 수정
  @PatchMapping("/{id}")
  public ApiResponse<?> updateUser(
      @PathVariable Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody UpdateRequest updateRequest) {

    userService.updateUser(id, userDetails, updateRequest);
    return ApiResponse.ok("회원 정보 수정 성공");
  }

  // 회원 탈퇴
  @PreAuthorize("hasRole('ROLE_USER')")
  @DeleteMapping("/{id}")
  public ApiResponse<?> deleteUser(
      @PathVariable Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody DeleteRequest deleteRequest,
      HttpServletResponse response,
      HttpServletRequest request) {
    userService.deleteUser(id, userDetails, deleteRequest, request, response);
    return ApiResponse.ok("회원 탈퇴 성공");
  }

  // 사용자 상태 조회 (feignClient)
  @GetMapping("/status/{email}")
  public UserStatusDto getUserStatus(@PathVariable("email") String email) {
    return userService.getUserStatus(email);
  }

  // 마일리지 업데이트
  @PostMapping("/mileage")
  public boolean updateMileage(@RequestBody UserRequestDto requestDto) {
    return userService.updateUserMileage(requestDto);
  }

}


