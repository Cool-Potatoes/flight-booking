package com.flight_booking.flight_service.presentation.response;

import com.flight_booking.flight_service.domain.model.Flight;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public record FlightResponseDto(UUID flightId, UUID departureAirportId, UUID arrivalAriportId,
                                LocalDateTime departureTime, LocalDateTime arrivalTime,
                                String state, Integer remainingSeat, String Airline,
                                Integer totalEconomySeatsCount, Integer totalBusinessSeatsCount,
                                Integer totalFirstSeatsCount) {

  @QueryProjection
  public FlightResponseDto(Flight flight) {
    this(
        flight.getFlightId(),
        flight.getDepartureAirport().getAirportId(),
        flight.getArrivalAirport().getAirportId(),
        flight.getDepartureTime(),
        flight.getArrivalTime(),
        flight.getStatusEnum().getState(),
        flight.getRemainingSeat(),
        flight.getAirline(),
        flight.getTotalEconomySeatsCount(),
        flight.getTotalBusinessSeatsCount(),
        flight.getTotalFirstClassSeatsCount()
    );
  }

  public static FlightResponseDto from(Flight savedFlight) {
    return new FlightResponseDto(savedFlight);
  }

  public static PagedModel<FlightResponseDto> from(Page<FlightResponseDto> flightResponseDtoPage) {
    return new PagedModel<>(flightResponseDtoPage);
  }

}
