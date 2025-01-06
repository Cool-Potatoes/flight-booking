package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.booking_service.domain.model.BookingStatusEnum;
import java.util.List;
import java.util.UUID;

public record BookingRequestDto(
    UUID flightId,
    List<PassengerRequestDto> passengerRequestDtos,
    BookingStatusEnum bookingStatus
) {
}
