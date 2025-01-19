package com.flight_booking.gateway_service.presentation.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode {

  EXPIRED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "만료된 토큰입니다."),
  UNSUPPORTED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "지원되지 않는 형식의 토큰입니다."),
  MALFORMED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 토큰입니다."),
  INVALID_SIGNATURE(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 서명입니다."),
  EMPTY_CLAIMS(HttpServletResponse.SC_UNAUTHORIZED, "토큰에 필요한 클레임 정보가 비어있습니다."),
  EMAIL_EXTRACTION_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "이메일 추출에 실패했습니다."),
  ROLE_EXTRACTION_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "역할 추출에 실패했습니다."),
  INVALID_SECRET_KEY(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SECRET_KEY 초기화에 실패했습니다."),
  BLACKLISTED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "해당 토큰은 블랙리스트에 포함되어 있습니다."),
  USER_BLOCKED(HttpServletResponse.SC_FORBIDDEN, "블락 처리된 회원입니다."),
  USER_DELETED(HttpServletResponse.SC_FORBIDDEN, "탈퇴한 회원입니다."),
  INVALID_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다."), // 추가된 메시지
  TOKEN_VALIDATION_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 검증 중 오류가 발생했습니다.");

  private final int statusCode;
  private final String message;

}
