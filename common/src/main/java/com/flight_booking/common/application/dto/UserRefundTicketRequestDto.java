package com.flight_booking.common.application.dto;

import java.util.UUID;

public record UserRefundTicketRequestDto(
    String email,
    UUID paymentId,
    Long refundFair,
    UUID bookingId,
    UUID passengerId,
    UUID seatId
) {

}