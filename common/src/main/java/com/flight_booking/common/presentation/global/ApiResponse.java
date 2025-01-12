package com.flight_booking.common.presentation.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private String message;
  private Integer httpStatus;
  private List<String> errorMessages;
  private String errorMessage;
  private T data;

  public static <T> ApiResponse<?> ok(T data, String message) {
    return ApiResponse.builder()
        .message(message)
        .httpStatus(HttpStatus.OK.value())
        .data(data)
        .build();
  }

  public static <T> ApiResponse<?> ok(String message) {
    return ApiResponse.builder()
        .message(message)
        .httpStatus(HttpStatus.OK.value())
        .build();
  }

  public static <T> ApiResponse<?> ok(T data) {
    return ApiResponse.builder()
        .httpStatus(HttpStatus.OK.value())
        .data(data)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .httpStatus(status.value())
        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static <T> ApiResponse<?> of(T data, String message, HttpStatus status) {
    return ApiResponse.builder()
        .message(message)
        .httpStatus(status.value())
        .data(data)
        .build();
  }

  public static ApiResponse<?> noContent() {
    return ApiResponse.builder()
        .httpStatus(HttpStatus.NO_CONTENT.value())
        .errorMessage("NO CONTENT")
        .data(null)
        .build();
  }
}