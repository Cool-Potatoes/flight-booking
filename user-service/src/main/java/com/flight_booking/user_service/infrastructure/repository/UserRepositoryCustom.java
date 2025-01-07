package com.flight_booking.user_service.infrastructure.repository;

import com.flight_booking.user_service.presentation.response.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

  Page<UserListResponse> findAll(
      String email, String name, String role, Boolean isBlocked, Pageable pageable);
}
