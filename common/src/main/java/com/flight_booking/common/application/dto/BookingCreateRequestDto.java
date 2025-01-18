package com.flight_booking.common.application.dto;

public record BookingCreateRequestDto(
    ReBookingRequestDto bookingRequestDto,
    String username
) {

}
