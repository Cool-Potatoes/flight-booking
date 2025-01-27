package com.flight_booking.gateway_service.application.dto;

public record UserStatusDto(

    Boolean isBlocked,
    Boolean isDeleted
) {

}
