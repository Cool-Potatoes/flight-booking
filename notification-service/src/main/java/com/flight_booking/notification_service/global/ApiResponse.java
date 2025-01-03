package com.flight_booking.notification_service.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 공통 API 응답 클래스
 * 모든 API 응답 형식을 통일하기 위해 사용
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 JSON 응답에서 제외
public class ApiResponse<T> {

  private final boolean success; // 요청 성공 여부
  private final HttpStatus httpStatus; // HTTP 상태 코드
  private final List<String> errors; // 에러 메시지 리스트
  private final T data; // 응답 데이터

  private ApiResponse(Builder<T> builder) {
    this.success = builder.success;
    this.httpStatus = builder.httpStatus;
    this.errors = builder.errors;
    this.data = builder.data;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private boolean success;
    private HttpStatus httpStatus;
    private List<String> errors;
    private T data;

    public Builder<T> success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder<T> httpStatus(HttpStatus httpStatus) {
      this.httpStatus = httpStatus;
      return this;
    }

    public Builder<T> errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponse<T> build() {
      return new ApiResponse<>(this);
    }
  }

  // 기존 정적 메서드도 빌더 패턴 기반으로 제공 가능
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