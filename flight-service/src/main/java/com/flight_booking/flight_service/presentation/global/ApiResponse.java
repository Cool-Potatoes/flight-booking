package com.flight_booking.flight_service.presentation.global;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
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

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .httpStatus(status.value())
        .errorMessages(errorMessages)
        .data(null)
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