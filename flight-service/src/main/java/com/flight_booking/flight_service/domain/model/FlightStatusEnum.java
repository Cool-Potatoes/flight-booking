package com.flight_booking.flight_service.domain.model;

public enum FlightStatusEnum {
  SCHEDULED("출발 전"),
  DEPARTED("출발"),
  DELAYED("연착"),
  CANCELLED("취소"),
  LANDED("도착");

  private final String state;

  FlightStatusEnum(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }
}
