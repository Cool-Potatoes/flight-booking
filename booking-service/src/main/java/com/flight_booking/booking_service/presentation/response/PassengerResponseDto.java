package com.flight_booking.booking_service.presentation.response;

import com.flight_booking.booking_service.domain.model.PassengerTypeEnum;
import java.util.UUID;

public record PassengerResponseDto(
    UUID seatId,
    PassengerTypeEnum passengerType,
    String passengerName,
    Boolean baggage,
    Boolean meal
) {

}