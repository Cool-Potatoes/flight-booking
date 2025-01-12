package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PaymentRefundRequestDto(
    @NotNull(message = "Email cannot be null") String email,
    @NotNull(message = "Booking ID cannot be null") UUID bookingId,
    @NotNull(message = "Passengers cannot be null") List<PassengerRequestDto> passengerRequestDtos,
    @NotNull(message = "newSeatTotalPrice cannot be null") Long newSeatTotalPrice
) {

}
