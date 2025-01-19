package com.flight_booking.common.presentation.dto;

import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import java.util.List;
import java.util.UUID;

public record BookingRequestDto(
    List<PassengerRequestDto> passengerRequestDtos,
    UUID ticketId
) {

}
