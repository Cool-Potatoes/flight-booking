package com.flight_booking.flight_service.presentation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.flight_service.domain.model.FlightStatusEnum;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlightRequestDto(
    String departureAirport,
    LocalDateTime departureTime,
    String arrivalAirport,
    LocalDateTime arrivalTime,
    FlightStatusEnum status,
    Integer remainingSeat,
    String airline,
    Integer totalEconomySeatsCount,
    Integer totalBusinessSeatsCount,
    Integer totalFirstClassSeatsCount
) {

}
