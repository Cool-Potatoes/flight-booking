package com.flight_booking.user_service.presentation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 사용자가 존재하지 않습니다."),
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
  LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다."),
  USER_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "사용자 인증에 실패했습니다."),
  INVALID_SECRET_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "SECRET_KEY 초기화에 실패했습니다."),
  ACCESS_ONLY_SELF(HttpStatus.FORBIDDEN, "본인만 가능합니다."),
  CANNOT_MODIFY_FIELD(HttpStatus.FORBIDDEN, "수정 불가능한 항목입니다.");  // 추가된 부분;

  private final HttpStatus httpStatus;
  private final String message;
}
