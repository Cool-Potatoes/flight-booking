package com.flight_booking.booking_service.presentation.response;

import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.model.PassengerTypeEnum;
import java.util.UUID;

public record PassengerResponseDto(
    UUID passengerId,
    UUID seatId,
    PassengerTypeEnum passengerType,
    String passengerName,
    Boolean baggage,
    Boolean meal
) {

  public static PassengerResponseDto of(Passenger passenger) {
    return new PassengerResponseDto(
        passenger.getPassengerId(),
        passenger.getSeatId(),
        passenger.getPassengerType(),
        passenger.getPassengerName(),
        passenger.getBaggage(),
        passenger.getMeal()
    );
  }
}