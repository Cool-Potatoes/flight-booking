package com.flight_booking.flight_service.presentation.response;

import com.flight_booking.flight_service.domain.model.FlightStatusEnum;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FlightDetailResponseDto(
    String depatureAirport,
    LocalDateTime depatureTime,
    String arrivalAirport,
    LocalDateTime arrivalTime,
    FlightStatusEnum status,
    String airline
) {

}
