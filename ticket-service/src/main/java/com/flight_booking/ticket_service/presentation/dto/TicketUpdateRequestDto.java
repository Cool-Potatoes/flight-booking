package com.flight_booking.ticket_service.presentation.dto;

import com.flight_booking.common.application.dto.PassengerRequestDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record TicketUpdateRequestDto (
  @NotNull(message = "Booking ID cannot be null")
  UUID bookingId,
  @NotNull(message = "Passenger ID cannot be null")
  UUID passengerId,
  @NotNull(message = "Seat ID cannot be null")
  UUID seatId,
  @NotNull(message = "PassengerRequestDtos cannot be null")
  List<PassengerRequestDto>passengerRequestDtos
){

}
