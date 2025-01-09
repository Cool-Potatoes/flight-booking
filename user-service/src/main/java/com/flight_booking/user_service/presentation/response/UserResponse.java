package com.flight_booking.user_service.presentation.response;

import com.flight_booking.user_service.domain.model.User;
import lombok.Builder;

@Builder
public record UserResponse(

    Long id,
    String email,
    String name,
    String phone,
    Long mileage,
    String role,
    Boolean isBlocked,
    Boolean isDeleted
) {

  public static UserResponse fromEntity(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .phone(user.getPhone())
        .mileage(user.getMileage())
        .role(user.getRole().name())
        .isBlocked(user.getIsBlocked())
        .isDeleted(user.getIsDeleted())
        .build();
  }
}
