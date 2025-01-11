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
  CANNOT_MODIFY_FIELD(HttpStatus.FORBIDDEN, "수정 불가능한 항목입니다."),
  USER_DELETED(HttpStatus.NOT_FOUND, "탈퇴한 사용자입니다."),
  USER_BLOCKED(HttpStatus.BAD_REQUEST, "블락 상태의 회원입니다."),
  USER_NOT_BLOCKED(HttpStatus.BAD_REQUEST, "블락 해제 상태의 회원입니다."),
  CANNOT_ADMIN_BLOCKED(HttpStatus.BAD_REQUEST, "관리자는 블락 설정 할 수 없습니다."),
  INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호와 일치하지 않습니다."),
  PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
  PASSWORDS_SAME(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다."),
  INVALID_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),
  CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 이메일에 대한 인증 코드가 존재하지 않습니다."),
  CODE_ALREADY_SENT(HttpStatus.BAD_REQUEST,"이미 인증 코드가 발급되었습니다. 1분 뒤에 다시 발급 가능합니다.");;

  private final HttpStatus httpStatus;
  private final String message;
}
