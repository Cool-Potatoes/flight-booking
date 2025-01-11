package com.flight_booking.user_service.presentation.response;

import com.flight_booking.user_service.domain.model.Role;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserListResponse(

    Long id,
    String email,
    String name,
    Role role,
    Boolean isBlocked,
    LocalDateTime createdAt

) {

}
