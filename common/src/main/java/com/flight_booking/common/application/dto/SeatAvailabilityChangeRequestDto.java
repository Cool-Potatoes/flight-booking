package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record SeatAvailabilityChangeRequestDto(
    @NotNull(message = "Email cannot be null") String email,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Booking ID cannot be null") List<PassengerRequestDto> passengerRequestDtos
) {

}