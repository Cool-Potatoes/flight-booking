package com.flight_booking.common.application.dto;

import java.util.UUID;

public record PaymentRefundFromTicketRequestDto(
    String email,
    UUID ticketId,
    UUID bookingId,
    UUID passengerId,
    UUID seatId
) {

}
