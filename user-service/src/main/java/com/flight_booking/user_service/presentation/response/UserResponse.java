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
    Boolean isBlocked
) {

  public static UserResponse fromEntity(User user) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getPhone(),
        user.getMileage(),
        user.getRole().name(),
        user.getIsBlocked()
    );
  }


}
