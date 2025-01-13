package com.flight_booking.common.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProcessTicketPaymentRequestDto(
    UUID paymentId,
    UUID seatId,
    UUID bookingId,
    UUID passengerId
) {

}