package com.flight_booking.common.application.dto;

import java.util.UUID;

public record SeatAvailabilityRefundRequestDto(
    UUID seatId
) {

}