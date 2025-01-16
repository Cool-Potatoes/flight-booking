package com.flight_booking.common.presentation.dto;

import com.flight_booking.common.application.dto.PassengerRequestDto;
import java.util.List;

public record BookingRequestDto(
    List<PassengerRequestDto> passengerRequestDtos
) {

}
