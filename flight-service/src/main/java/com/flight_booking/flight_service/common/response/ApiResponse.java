package com.flight_booking.flight_service.common.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private int httpStatus;
  private T data;

  public static <T> ApiResponse<?> ok(T data) {
    return ApiResponse.builder()
        .success(true)
        .httpStatus(HttpStatus.OK.value())
//        .errorMessages(null)
        .data(data)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, List<String> errorMessages) {
    return ApiResponse.builder()
        .success(false)
        .httpStatus(status.value())
//        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static ApiResponse<?> of(HttpStatus status, String errorMessage) {
    List<String> errorMessages = List.of(errorMessage);

    return ApiResponse.builder()
        .success(false)
        .httpStatus(status.value())
//        .errorMessages(errorMessages)
        .data(null)
        .build();
  }

  public static ApiResponse<?> noContent() {

    return ApiResponse.builder()
        .success(false)
        .httpStatus(HttpStatus.NO_CONTENT.value())
//        .errorMessage("NO CONTENT")
        .data(null)
        .build();
  }
}