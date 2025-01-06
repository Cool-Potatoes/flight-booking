package com.flight_booking.gateway_service.util;

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
  INVALID_SECRET_KEY(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SECRET_KEY 초기화에 실패했습니다.");

  private final int statusCode;
  private final String message;

}
