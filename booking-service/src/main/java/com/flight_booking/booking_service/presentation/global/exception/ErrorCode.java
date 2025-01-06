package com.flight_booking.booking_service.presentation.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // BOOKING
  NOT_FOUND_BOOKING_EXCEPTION(401, "유효하지 않은 예약 입니다."),
  NOT_FOUND_BOOKING_ID_EXCEPTION(401, "BOOKING ID 를 찾을 수 없습니다."),
  NO_AUTHORIZATION_EXCEPTION(403, "접근 권한이 없습니다."),

  // PASSENGER
  NOT_FOUND_PASSENGER_EXCEPTION(401, "유효하지 않은 승객 입니다."),
  INVALID_PASSENGER_LIST_EXCEPTION(402, "승객 목록이 비어 있거나 유효하지 않습니다."),
  MISSING_REQUIRED_FIELDS_EXCEPTION(403, "승객의 필수 필드가 누락되었습니다.");

  ;
  private final int status;

  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

}
