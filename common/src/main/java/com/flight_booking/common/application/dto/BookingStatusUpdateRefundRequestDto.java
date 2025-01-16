package com.flight_booking.common.application.dto;

import com.flight_booking.common.domain.model.BookingStatusEnum;
import java.util.UUID;

public record BookingStatusUpdateRefundRequestDto(
    UUID bookingId,
    BookingStatusEnum bookingStatusEnum){

}
