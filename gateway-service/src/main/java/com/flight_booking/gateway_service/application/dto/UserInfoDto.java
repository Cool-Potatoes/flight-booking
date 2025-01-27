package com.flight_booking.gateway_service.application.dto;

import lombok.Builder;

@Builder
public record UserInfoDto(
    String email,
    String role,
    Boolean isBlocked,
    Boolean isDeleted
) {

}
