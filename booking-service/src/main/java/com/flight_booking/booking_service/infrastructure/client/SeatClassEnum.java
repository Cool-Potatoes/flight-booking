package com.flight_booking.booking_service.infrastructure.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatClassEnum {

  ECONOMY("SEAT_ECONOMY"),
  BUSINESS("SEAT_BUSINESS");

  private final String seatClass;
}
