package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import java.util.List;

public record BookingUpdateRequestDto(
    List<PassengerRequestDto> passengerRequestDtos,
    BookingStatusEnum bookingStatusEnum
) {

}
