package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.response.PageResponse;
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
  public PageResponse<UserListResponse> getUserList(String email, String name, String role, Boolean isBlocked, Pageable pageable) {
    Page<UserListResponse> users = userRepository.findAll(email, name, role, isBlocked, pageable);

    return PageResponse.from(users);
  }
}
