package com.flight_booking.user_service.application.dto;

public record UserStatusDto(

    Boolean isBlocked,
    Boolean isDeleted
) {

}
