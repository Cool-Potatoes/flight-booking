package com.flight_booking.flight_service.presentation.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FlightListResponseDto(
    UUID flightId,
    String depatureAirport,
    LocalDateTime depatureTime,
    String arrivalAirport,
    LocalDateTime arrivalTime,
    String airline
) {

}
