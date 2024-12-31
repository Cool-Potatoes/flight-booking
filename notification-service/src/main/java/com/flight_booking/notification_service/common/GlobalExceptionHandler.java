package com.flight_booking.notification_service.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // [1] Validation 실패 예외 처리
  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ApiResponse<?>> handleBindException(BindException e) {
    log.error("Validation Error: {}", e.toString()); // getMessage() 대신 toString() 사용
    BindingResult bindingResult = e.getBindingResult();
    List<String> errors = bindingResult.getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(ApiResponse.of(HttpStatus.BAD_REQUEST, errors));
  }

  // [2] 지원되지 않는 HTTP 메서드 예외 처리
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error("Method Not Allowed");
    String supportedMethods = e.getSupportedHttpMethods() != null
        ? e.getSupportedHttpMethods().toString()
        : "지원되는 메서드 없음";
    String message = "지원되지 않는 HTTP 메서드입니다. 지원되는 메서드: " + supportedMethods;
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ApiResponse.of(HttpStatus.METHOD_NOT_ALLOWED, message));
  }

  // [3] 지원되지 않는 Media Type 예외 처리
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  protected ResponseEntity<ApiResponse<?>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException e) {
    log.error("Media Type Not Supported");
    e.getSupportedMediaTypes();
    String supportedMediaTypes = e.getSupportedMediaTypes().toString();
    String message = "지원되지 않는 미디어 타입입니다. 지원되는 타입: " + supportedMediaTypes;
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(ApiResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message));
  }

  // [4] 일반적인 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
    log.error("Unhandled Exception: {}", e.toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 오류가 발생했습니다."));
  }
}
