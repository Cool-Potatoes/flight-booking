package com.flight_booking.notification_service.common;

import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * 공통 API 응답 클래스
 * 모든 API 응답 형식을 통일하기 위해 사용
 */
public class ApiResponse<T> {

  private boolean success; // 요청 성공 여부
  private HttpStatus httpStatus; // HTTP 상태 코드
  private List<String> errors; // 에러 메시지 리스트
  private T data; // 응답 데이터

  private ApiResponse() {}

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private final ApiResponse<T> apiResponse;

    public Builder() {
      apiResponse = new ApiResponse<>();
    }

    public Builder<T> success(boolean success) {
      apiResponse.success = success;
      return this;
    }

    public Builder<T> httpStatus(HttpStatus httpStatus) {
      apiResponse.httpStatus = httpStatus;
      return this;
    }

    public Builder<T> errors(List<String> errors) {
      apiResponse.errors = errors;
      return this;
    }

    public Builder<T> data(T data) {
      apiResponse.data = data;
      return this;
    }

    public ApiResponse<T> build() {
      return apiResponse;
    }
  }

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .httpStatus(HttpStatus.OK)
        .data(data)
        .build();
  }

  public static ApiResponse<?> error(HttpStatus status, List<String> errors) {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(status)
        .errors(errors)
        .build();
  }

  public static ApiResponse<?> noContent() {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(HttpStatus.NO_CONTENT)
        .errors(List.of("No content available"))
        .build();
  }
}
