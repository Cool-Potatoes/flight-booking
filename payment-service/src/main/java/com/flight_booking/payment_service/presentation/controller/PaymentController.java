package com.flight_booking.payment_service.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.presentation.request.UpdateFareRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @KafkaListener(groupId = "payment-service-group", topics = "payment-creation-topic")
  public ApiResponse<?> consumePaymentCreation(@Payload ApiResponse<PaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(), PaymentRequestDto.class);

    PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);

    return ApiResponse.ok(paymentResponseDto, "결제 데이터 생성 성공");
  }

  @PostMapping
  public ApiResponse<?> createPayment(
      @RequestBody @Valid PaymentRequestDto paymentRequestDto) {

    PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);

    return ApiResponse.ok(paymentResponseDto, "결제 데이터 생성 성공");
  }

  @GetMapping("/{paymentId}")
  public ApiResponse<?> getPayment(
      @PathVariable UUID paymentId) {

    PaymentResponseDto paymentResponseDto = paymentService.getPayment(paymentId);

    return ApiResponse.ok(paymentResponseDto, "결제 데이터 조회 성공");
  }

  @GetMapping
  public ApiResponse<?> getPaymentsPage(
      @RequestParam(required = false) List<UUID> uuidList,
      @QuerydslPredicate(root = Payment.class) Predicate predicate,
      Pageable pageable
  ) {

    PagedModel<PaymentResponseDto> paymentResponseDtoPagedModel
        = paymentService.getProductsPage(uuidList, predicate, pageable);

    return ApiResponse.ok(paymentResponseDtoPagedModel, "결제 데이터 목록 조회 성공");
  }

  @PutMapping("/{paymentId}")
  public ApiResponse<?> updateFare(
      @RequestBody @Valid UpdateFareRequestDto updateFareRequestDto,
      @PathVariable UUID paymentId
  ) {

    PaymentResponseDto paymentResponseDto
        = paymentService.updateFare(updateFareRequestDto, paymentId);

    return ApiResponse.ok(paymentResponseDto, "결제 금액 데이터 수정 성공");
  }

  @DeleteMapping("/{paymentId}")
  public ApiResponse<?> deletePayment(
      @PathVariable UUID paymentId
  ) {

    paymentService.deletePayment(paymentId);

    return ApiResponse.ok("결제 삭제 성공");

  }
}
