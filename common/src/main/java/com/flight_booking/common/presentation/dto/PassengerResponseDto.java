package com.flight_booking.common.presentation.dto;

import com.flight_booking.common.domain.model.PassengerTypeEnum;
import java.util.UUID;

public record PassengerResponseDto(
    UUID passengerId,
    UUID seatId,
    PassengerTypeEnum passengerType,
    String passengerName,
    Boolean baggage,
    Boolean meal
) {

  public static PassengerResponseDto from(
      UUID passengerId,
      UUID seatId,
      PassengerTypeEnum passengerType,
      String passengerName,
      Boolean baggage,
      Boolean meal
  ) {
    return new PassengerResponseDto(passengerId, seatId, passengerType, passengerName, baggage,
        meal);
  }
}