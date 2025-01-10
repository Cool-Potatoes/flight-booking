package com.flight_booking.user_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.BlockService;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.presentation.request.BlockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class BlockController {

  private final BlockService blockService;

  // 회원 블락 처리 (관리자용)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/{id}/block")
  public ApiResponse<?> blockUser(
      @PathVariable Long id,
      @Valid @RequestBody BlockRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    blockService.blockUser(id, request, userDetails);
    return ApiResponse.ok("회원 블락 처리 성공");
  }

  // 회원 블락 해제 (관리자용)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/{id}/unblock")
  public ApiResponse<?> unblockUser(
      @PathVariable Long id) {
    blockService.unblockUser(id);
    return ApiResponse.ok("회원 블락 해제 성공");
  }

}
