package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.AdminUserDetailResponse;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserDetailResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  // 전체 회원 목록 조회
  @Transactional(readOnly = true)
  public PageResponse<UserListResponse> getUserList(String email, String name, String role,
      Boolean isBlocked, Pageable pageable) {
    Page<UserListResponse> users = userRepository.findAll(email, name, role, isBlocked, pageable);

    return PageResponse.from(users);
  }

  // 회원 상세 정보 조회
  @Transactional(readOnly = true)
  public Object getUserDetails(Long id, CustomUserDetails userDetails) {
    User user = getUser(id);
    boolean isAdmin = isAdmin(userDetails);

    // 관리자의 경우
    if (isAdmin) {
      return AdminUserDetailResponse.fromEntity(user);
    }

    // 사용자의 경우 본인 정보만 조회 가능
    checkUser(userDetails, user);
    return UserDetailResponse.fromEntity(user);
  }

  // -----------------------------------------------------------------------------------------------

  // 존재하는 사용자 확인
  private User getUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.ACCESS_ONLY_SELF));
  }

  // 관리자 권한 확인
  private boolean isAdmin(CustomUserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
  }

  // 사용자 본인 확인
  private void checkUser(CustomUserDetails userDetails, User user) {
    if (!userDetails.getUsername().equals(user.getEmail())) {
      throw new UserException(ErrorCode.ACCESS_ONLY_SELF);
    }
  }
}
