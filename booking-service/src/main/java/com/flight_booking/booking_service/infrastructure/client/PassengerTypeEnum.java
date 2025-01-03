package com.flight_booking.booking_service.infrastructure.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PassengerTypeEnum {

  ADULT("PASSENGER_ADULT"),
  CHILD("PASSENGER_CHILD");

  private final String passengerType;
}