package com.flight_booking.common.application.dto;

import java.util.UUID;

public record BookingRefundRequestDto(
    UUID bookingId,
    UUID seatId,
    UUID passengerId

) {

}
