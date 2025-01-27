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

  // 블락 사유 응답용
  public UserException(ErrorCode errorCode, String additionalMessage) {
    super(errorCode.getMessage() + ": " + additionalMessage);
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorCode.getMessage() + " - " + additionalMessage;
  }
}
