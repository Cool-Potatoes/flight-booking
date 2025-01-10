package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SeatBookingRequestDto(
    @NotNull(message = "Booking ID cannot be null") String email,
    @NotNull(message = "BookingStatus cannot be null") UUID bookingId) {

}