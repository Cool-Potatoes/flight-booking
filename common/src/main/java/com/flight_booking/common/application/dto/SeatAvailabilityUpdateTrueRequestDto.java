package com.flight_booking.common.application.dto;

import java.util.UUID;

public record SeatAvailabilityUpdateTrueRequestDto(
    UUID seatId,
    Boolean available){

}
