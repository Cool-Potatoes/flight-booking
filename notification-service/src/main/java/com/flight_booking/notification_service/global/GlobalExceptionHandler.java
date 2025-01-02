package com.flight_booking.notification_service.global;

import com.flight_booking.notification_service.global.exception.NotificationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 클래스
 * 모든 컨트롤러에서 발생하는 예외를 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Validation 실패 예외 처리
  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ApiResponse<?>> handleBindException(BindException e) {
    log.error("Validation Error: {}", e.getMessage());
    List<String> errors = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, errors));
  }

  // 사용자 정의 예외 처리
  @ExceptionHandler(NotificationNotFoundException.class)
  protected ResponseEntity<ApiResponse<?>> handleNotificationNotFoundException(
      NotificationNotFoundException e) {
    log.error("Notification Not Found: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(HttpStatus.NOT_FOUND, List.of(e.getMessage())));
  }

  // 일반적인 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
    log.error("Unhandled Exception: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, List.of("An unexpected error occurred")));
  }
}