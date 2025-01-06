package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.booking_service.domain.model.PassengerTypeEnum;
import java.util.UUID;

public record PassengerRequestDto(
    UUID seatId,
    PassengerTypeEnum passengerType,
    String passengerName,
    Boolean baggage,
    Boolean meal
) {

}