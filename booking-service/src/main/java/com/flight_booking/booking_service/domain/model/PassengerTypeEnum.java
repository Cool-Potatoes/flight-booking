package com.flight_booking.booking_service.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PassengerTypeEnum {

  ADULT("PASSENGER_ADULT"),
  CHILD("PASSENGER_CHILD");

  private final String passengerType;
}