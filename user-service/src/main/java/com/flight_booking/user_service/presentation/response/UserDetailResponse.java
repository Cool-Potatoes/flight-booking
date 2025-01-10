package com.flight_booking.user_service.presentation.response;

import com.flight_booking.user_service.domain.model.User;
import lombok.Builder;

@Builder
public record UserDetailResponse(

    String email,
    String name,
    String phone,
    Long mileage

) {

  public static UserDetailResponse fromEntity(User user) {
    return new UserDetailResponse(
        user.getEmail(),
        user.getName(),
        user.getPhone(),
        user.getMileage()
    );
  }

}
