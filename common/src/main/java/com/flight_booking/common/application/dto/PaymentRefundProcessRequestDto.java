package com.flight_booking.common.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentRefundProcessRequestDto(
    UUID ticketId,
    @NotNull(message = "Payment ID cannot be null") UUID paymentId,
    List<PassengerRequestDto> passengerRequestDtos,
    @NotNull(message = "Email cannot be null") String email
) {

}