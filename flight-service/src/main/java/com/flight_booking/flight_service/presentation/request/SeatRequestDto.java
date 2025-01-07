package com.flight_booking.flight_service.presentation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.flight_service.domain.model.SeatClassEnum;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SeatRequestDto(
    String seatNumber,
    SeatClassEnum seatClass, // ECONOMY, BUSINESS, FIRST
    Integer price,
    Boolean isAvailable,
    UUID flightId
) {

}

