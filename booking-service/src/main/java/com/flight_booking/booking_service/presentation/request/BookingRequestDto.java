package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.common.domain.model.BookingStatusEnum;
import java.util.List;
import java.util.UUID;

public record BookingRequestDto(
    UUID seatId,
    List<PassengerRequestDto> passengerRequestDtos,
    BookingStatusEnum bookingStatus
) {
}
