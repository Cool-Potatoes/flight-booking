package com.flight_booking.common.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRequestDto(
    @NotNull(message = "User Email cannot be null") String email,
    @NotNull(message = "Fair cannot be null") Long fare,
    @NotNull(message = "Payment ID cannot be null") UUID paymentId) {

}
