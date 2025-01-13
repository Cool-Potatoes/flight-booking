package com.flight_booking.common.application.dto;

public record NotificationRequestDto(
    Long userId,
    String receiverEmail,
    String code
) {

}
