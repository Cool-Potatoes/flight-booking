package com.flight_booking.flight_service.presentation.response;

import com.flight_booking.flight_service.domain.model.Flight;
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

  public static FlightDetailResponseDto from(Flight flight) {
    return FlightDetailResponseDto.builder()
        .depatureAirport(flight.getDepartureAirport().getCityName())
        .depatureTime(flight.getDepartureTime())
        .arrivalAirport(flight.getArrivalAirport().getCityName())
        .arrivalTime(flight.getArrivalTime())
        .status(flight.getStatus())
        .airline(flight.getAirline()).build();

  }
}
