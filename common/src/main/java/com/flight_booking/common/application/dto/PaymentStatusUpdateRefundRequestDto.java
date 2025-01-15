package com.flight_booking.common.application.dto;

import java.util.UUID;

public record PaymentStatusUpdateRefundRequestDto(
    UUID bookingId
){

}
