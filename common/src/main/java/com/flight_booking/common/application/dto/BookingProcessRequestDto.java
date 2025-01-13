package com.flight_booking.common.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingProcessRequestDto(
    UUID ticketId,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    List<PassengerRequestDto> passengerRequestDtos,
    @NotNull(message = "Email cannot be null") String email
    ) {

}