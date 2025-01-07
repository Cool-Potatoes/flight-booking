package com.flight_booking.user_service.presentation.response;

import com.flight_booking.user_service.domain.model.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AdminUserDetailResponse(

    Long id,
    String email,
    String name,
    String phone,
    Long mileage,
    String role,
    Boolean isBlocked,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy

) {

  public static AdminUserDetailResponse fromEntity(User user) {
    return new AdminUserDetailResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getPhone(),
        user.getMileage(),
        user.getRole().getAuthority(),
        user.getIsBlocked(),
        user.getCreatedAt(),
        user.getCreatedBy(),
        user.getUpdatedAt(),
        user.getUpdatedBy()
    );
  }


}
