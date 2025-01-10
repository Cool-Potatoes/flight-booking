package com.flight_booking.user_service.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserDetailResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
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

    // 사용자는 본인 정보만 제공
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
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    userService.deleteUser(id, userDetails);
    return ApiResponse.ok("회원 탈퇴 성공");
  }

  @KafkaListener(groupId = "payment-mile-group", topics = "payment-mile-topic")
  public ApiResponse<?> consumeMileageUpdate(@Payload ApiResponse<UserRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    UserRequestDto userRequestDto = mapper.convertValue(message.getData(),
        UserRequestDto.class);

    UserDetailResponse userResponse = userService.updateUserMileage(userRequestDto);

    return ApiResponse.ok(userResponse, "유저 마일리지 변경 성공");
  }
}


