package com.flight_booking.notification_service.common;

import org.springframework.http.HttpStatus;
import java.util.List;

public class ApiResponse<T> {

  private boolean success;
  private HttpStatus httpStatus;
  private List<String> errorMessages;
  private String errorMessage;
  private T data;

  // Private 생성자
  private ApiResponse() {}

  // Static Builder 메서드 시작점
  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  // Builder 내부 클래스
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

    public Builder<T> errorMessages(List<String> errorMessages) {
      apiResponse.errorMessages = errorMessages;
      return this;
    }

    public Builder<T> errorMessage(String errorMessage) {
      apiResponse.errorMessage = errorMessage;
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

  // Static Factory Methods
  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .httpStatus(HttpStatus.OK)
        .data(data)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(status)
        .errorMessages(errorMessages)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, String errorMessage) {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(status)
        .errorMessage(errorMessage)
        .build();
  }

  public static ApiResponse<?> noContent() {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(HttpStatus.NO_CONTENT)
        .errorMessage("NO CONTENT")
        .build();
  }
}
