package com.flight_booking.common.application.dto;

import com.flight_booking.common.domain.model.BookingStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public record BookingProcessRequestDto(
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "BookingStatus cannot be null") BookingStatusEnum bookingStatus) {
}