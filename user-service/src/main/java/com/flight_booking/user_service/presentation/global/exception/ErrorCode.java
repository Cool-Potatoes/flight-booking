package com.flight_booking.user_service.presentation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 사용자가 존재하지 않습니다."),
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
  LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
