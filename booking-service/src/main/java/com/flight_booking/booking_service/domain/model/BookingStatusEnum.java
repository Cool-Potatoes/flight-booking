package com.flight_booking.booking_service.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatusEnum {

  BOOKING("BOOKING_COMPLETE"),
  CANCELLED("BOOKING_CANCELLED");

  private final String booking;
}
