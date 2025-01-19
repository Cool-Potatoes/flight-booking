package com.flight_booking.gateway_service.application;

public record UserStatusDto(

    Boolean isBlocked,
    Boolean isDeleted
) {

}
