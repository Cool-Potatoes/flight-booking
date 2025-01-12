package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record SeatBookingRequestDto(
    @NotNull(message = "Email cannot be null") String email,
    @NotNull(message = "Booking Id cannot be null") UUID bookingId,
    @NotNull(message = "Seat Id cannot be null") List<UUID> seatIdList) {

}