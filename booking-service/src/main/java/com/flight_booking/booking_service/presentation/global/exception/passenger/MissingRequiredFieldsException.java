package com.flight_booking.booking_service.presentation.global.exception.passenger;

import com.flight_booking.booking_service.presentation.global.exception.BusinessException;
import com.flight_booking.booking_service.presentation.global.exception.ErrorCode;

public class MissingRequiredFieldsException extends BusinessException {

  public MissingRequiredFieldsException() {
    super(ErrorCode.MISSING_REQUIRED_FIELDS_EXCEPTION);
  }
}
