package com.flight_booking.common.application.dto;

import com.flight_booking.common.domain.model.PaymentStatusEnum;
import java.util.UUID;

public record PaymentStatusUpdateRefundRequestDto(
    UUID bookingId,
    PaymentStatusEnum paymentStatusEnum){

}
