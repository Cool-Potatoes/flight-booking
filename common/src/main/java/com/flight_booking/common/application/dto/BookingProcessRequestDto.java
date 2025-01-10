package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookingProcessRequestDto(
    @NotNull(message = "Booking ID cannot be null") UUID bookingId) {

}