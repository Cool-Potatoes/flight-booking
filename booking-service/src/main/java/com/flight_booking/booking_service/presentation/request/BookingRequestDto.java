package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.common.application.dto.PassengerRequestDto;
import java.util.List;
import java.util.UUID;

public record BookingRequestDto(
    List<UUID> seatId,
    List<PassengerRequestDto> passengerRequestDtos
) {

}
