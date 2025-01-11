package com.flight_booking.user_service.presentation.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {

  private final HttpStatus httpStatus;
  private final String errorMessage;

  public UserException(ErrorCode errorCode) {
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorCode.getMessage();
  }

  // 수정된 생성자 (메시지 추가)
  public UserException(ErrorCode errorCode, String errorMessage) {
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorMessage;
  }
}
