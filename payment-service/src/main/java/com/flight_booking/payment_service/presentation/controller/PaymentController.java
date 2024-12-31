package com.flight_booking.payment_service.presentation.controller;

import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.presentation.global.ApiResponse;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ApiResponse<?> createPayment(
      @RequestBody PaymentRequestDto paymentRequestDto) {

    PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);

    return ApiResponse.ok(paymentResponseDto);
  }

  @GetMapping("/{paymentId}")
  public ApiResponse<?> getPayment(
      @PathVariable UUID paymentId) {

    PaymentResponseDto paymentResponseDto = paymentService.getPayment(paymentId);

    return ApiResponse.ok(paymentResponseDto);
  }

  @GetMapping
  public ApiResponse<?> getPaymentsPage(
      @RequestParam(required = false) List<UUID> uuidList,
      @QuerydslPredicate(root = Payment.class) Predicate predicate,
      Pageable pageable
  ) {

    PagedModel<PaymentResponseDto> paymentResponseDtoPagedModel
        = paymentService.getProductsPage(uuidList, predicate, pageable);

    return ApiResponse.ok(paymentResponseDtoPagedModel);
  }

  @PutMapping("/{paymentId}")
  public ApiResponse<?> updateFare(
      @RequestBody PaymentRequestDto paymentRequestDto,
      @PathVariable UUID paymentId
  ) {

    PaymentResponseDto paymentResponseDto
        = paymentService.updateFare(paymentRequestDto, paymentId);

    return ApiResponse.ok(paymentResponseDto);
  }

  @DeleteMapping("/{paymentId}")
  public ApiResponse<?> deletePayment(
      @PathVariable UUID paymentId
  ) {

    paymentService.deletePayment(paymentId);

    return ApiResponse.ok("결제 삭제 성공");

  }
}
