package com.flight_booking.booking_service.presentation.global.exception.booking;

import com.flight_booking.booking_service.presentation.global.exception.BusinessException;
import com.flight_booking.booking_service.presentation.global.exception.ErrorCode;

public class NotFoundBookingIdException extends BusinessException {

  public NotFoundBookingIdException() {
    super(ErrorCode.NOT_FOUND_BOOKING_ID_EXCEPTION);
  }
}