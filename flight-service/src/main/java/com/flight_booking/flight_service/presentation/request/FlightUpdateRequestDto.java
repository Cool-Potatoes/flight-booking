package com.flight_booking.flight_service.presentation.request;

import com.flight_booking.flight_service.domain.model.FlightStatusEnum;
import java.time.LocalDateTime;

public record FlightUpdateRequestDto(
    Integer remainingSeat,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime,
    FlightStatusEnum status
) {

}
