package com.flight_booking.flight_service.presentation.request;

import com.flight_booking.flight_service.domain.model.FlightStatusEnum;
import java.time.LocalDateTime;


public record FlightCreateRequestDto(
    String departureAirport,
    LocalDateTime departureTime,
    String arrivalAirport,
    LocalDateTime arrivalTime,
    FlightStatusEnum status,
    Integer remainingSeat,
    String airline
) {

}
