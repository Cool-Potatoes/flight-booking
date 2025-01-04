package com.flight_booking.booking_service.presentation.global.exception.passenger;

import com.flight_booking.booking_service.presentation.global.exception.BusinessException;
import com.flight_booking.booking_service.presentation.global.exception.ErrorCode;

public class InvalidPassengerListException extends BusinessException {

  public InvalidPassengerListException() {
    super(ErrorCode.INVALID_PASSENGER_LIST_EXCEPTION);
  }
}
