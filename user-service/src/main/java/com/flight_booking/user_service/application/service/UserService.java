package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
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

  // 관리자일 경우 모든 사용자 정보 제공
  public AdminUserDetailResponse getAdminUserDetails(Long id) {
    User user = getUser(id);
    return AdminUserDetailResponse.fromEntity(user);
  }

  // 사용자의 경우 본인 정보만 조회
  public UserDetailResponse getUserDetails(Long id) {
    User user = getUser(id);
    return UserDetailResponse.fromEntity(user);
  }

  // 사용자 조회
  private User getUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.ACCESS_ONLY_SELF));
  }

}
