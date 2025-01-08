package com.flight_booking.flight_service.domain.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SeatClassEnum {
  ECONOMY("SEAT_ECONOMY"),
  BUSINESS("SEAT_BUSINESS"),
  FIRST("SEAT_FIRST");

  private final String classType;

  public String getClassType() {
    return classType;
  }
}
