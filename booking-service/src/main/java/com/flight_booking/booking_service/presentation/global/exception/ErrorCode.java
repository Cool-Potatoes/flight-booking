package com.flight_booking.booking_service.presentation.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // BOOKING
  NOT_FOUND_BOOKING_EXCEPTION(401, "유효하지 않은 예약 입니다."),
  NOT_FOUND_BOOKING_ID_EXCEPTION(401, "BOOKING ID 를 찾을 수 없습니다."),
  NO_AUTHORIZATION_EXCEPTION(403, "접근 권한이 없습니다.");


  private final int status;

  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

}
