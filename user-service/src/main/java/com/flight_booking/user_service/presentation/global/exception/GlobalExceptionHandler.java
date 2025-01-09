package com.flight_booking.user_service.presentation.global.exception;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserException.class)
  protected ResponseEntity<ApiResponse<?>> handleUserException(UserException e) {

    log.error("UserException", e);

    HttpStatus httpStatus = e.getHttpStatus();
    ApiResponse<?> apiResponse = ApiResponse.of(httpStatus, List.of(e.getErrorMessage()));

    return ResponseEntity.status(httpStatus).body(apiResponse);
  }


  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ApiResponse<?>> handleBindException(BindException e) {

    log.error("handleBindException", e);

    BindingResult bindingResult = e.getBindingResult();

    List<String> errorMessages = bindingResult.getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());

    ApiResponse<?> apiResponse = ApiResponse.of(HttpStatus.BAD_REQUEST, errorMessages);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {

    log.error("handleHttpRequestMethodNotSupportedException", e);

    ApiResponse<?> apiResponse = ApiResponse.of(HttpStatus.METHOD_NOT_ALLOWED,
        List.of(e.getMessage()));

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiResponse);
  }

//  @ExceptionHandler(BusinessException.class)
//  protected ResponseEntity<ApiResponse<?>> handleConflict(BusinessException e) {
//
//    log.error("BusinessException", e);
//
//    HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus());
//    ApiResponse<?> apiResponse = ApiResponse.of(httpStatus, List.of(e.getMessage()));
//
//    return ResponseEntity.status(httpStatus).body(apiResponse);
//  }

  @ExceptionHandler(HttpMessageConversionException.class)
  protected ResponseEntity<ApiResponse<?>> handleHttpMessageConversionException(
      HttpMessageConversionException e) {

    log.error("HttpMessageConversionException", e);

    ApiResponse<?> apiResponse = ApiResponse.of(HttpStatus.BAD_REQUEST,
        List.of(e.getMessage()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  protected ResponseEntity<ApiResponse<?>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException e) {

    log.error("HttpMediaTypeNotSupportedException", e);

    ApiResponse<?> apiResponse = ApiResponse.of(HttpStatus.BAD_REQUEST,
        List.of(e.getMessage()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {

    log.error("Exception", e);

    ApiResponse<?> apiResponse = ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
        List.of(e.getMessage()));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
  }


}