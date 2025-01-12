package com.flight_booking.common.application.dto;

import com.flight_booking.common.domain.model.PassengerTypeEnum;
import java.util.UUID;

public record PassengerRequestDto(
    UUID seatId,
    PassengerTypeEnum passengerType,
    String passengerName,
    Boolean baggage,
    Boolean meal
) {

}