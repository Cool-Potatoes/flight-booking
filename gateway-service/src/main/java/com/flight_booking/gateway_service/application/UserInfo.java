package com.flight_booking.gateway_service.application;

import lombok.Builder;

@Builder
public record UserInfo(
    String email,
    String role,
    Boolean isBlocked,
    Boolean isDeleted
) {

}
