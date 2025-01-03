package com.flight_booking.user_service.presentation.global;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ApiResponse<T> {

  private String message;
  private HttpStatus httpStatus;
  private List<String> errorMessages;
  private String errorMessage;
  private T data;

  public static <T> ApiResponse<?> ok(T data, String message) {
    return ApiResponse.builder()
        .httpStatus(HttpStatus.OK)  // HttpStatus OK로 설정
        .errorMessages(null)
        .data(data)
        .message(message)  // 메시지 추가
        .build();
  }

  public static <T> ApiResponse<?> ok(String message) {
    return ApiResponse.builder()
        .message(message)
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .httpStatus(status)
        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static ApiResponse<?> noContent() {
    return ApiResponse.builder()
        .httpStatus(HttpStatus.NO_CONTENT)
        .errorMessage("NO CONTENT")
        .data(null)
        .build();
  }
}