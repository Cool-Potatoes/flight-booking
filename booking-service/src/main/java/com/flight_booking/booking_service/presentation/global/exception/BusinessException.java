package com.flight_booking.booking_service.presentation.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final int status;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.status = errorCode.getStatus();
  }

  public BusinessException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.status = errorCode.getStatus();
  }
}
