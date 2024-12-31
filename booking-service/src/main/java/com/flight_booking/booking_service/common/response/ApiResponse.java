package com.flight_booking.booking_service.common.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
public class ApiResponse<T> {

  private boolean success;
  private HttpStatus httpStatus;
  private List<String> errorMessages;
  private String errorMessage;
  private T data;

  public static <T> ApiResponse<?> ok(T data) {
    return ApiResponse.builder()
        .success(true)
        .httpStatus(HttpStatus.OK)
        .errorMessages(null)
        .data(data)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(status)
        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, String errorMessage) {
    List<String> errorMessages = List.of(errorMessage);

    return ApiResponse.builder()
        .success(false)
        .httpStatus(status)
        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static ApiResponse<?> noContent() {

    return ApiResponse.builder()
        .success(false)
        .httpStatus(HttpStatus.NO_CONTENT)
        .errorMessage("NO CONTENT")
        .data(null)
        .build();
  }
}