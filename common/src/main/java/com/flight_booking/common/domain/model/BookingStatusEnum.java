package com.flight_booking.common.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatusEnum {

  BOOKING_WAITING("BOOKING_WAITING"),
  BOOKING_COMPLETE("BOOKING_COMPLETE"),
  BOOKING_CHANGE_PENDING("BOOKING_PROCESS_UPDATE"),
  BOOKING_CANCELLED("BOOKING_CANCELLED"),
  BOOKING_FAIL("BOOKING_FAIL"),
  BOOKING_REFUND_FAIL("BOOKING_REFUND_FAIL");

  private final String booking;
}
