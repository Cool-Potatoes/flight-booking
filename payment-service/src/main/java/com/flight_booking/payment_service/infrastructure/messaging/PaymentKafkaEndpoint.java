package com.flight_booking.payment_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRefundFromTicketRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundProcessRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.PaymentStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.ProcessTicketPaymentRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentKafkaEndpoint {

  private final PaymentService paymentService;
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "payment-service-group", topics = "payment-creation-topic")
  public void consumePaymentCreation(@Payload ApiResponse<PaymentRequestDto> message) {

    PaymentRequestDto paymentRequestDto = objectMapper.convertValue(message.getData(),
        PaymentRequestDto.class);

    paymentService.createPayment(paymentRequestDto);
  }

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "payment-process-group", topics = "payment-success-process-topic")
  public void consumePaymentSuccessProcess(
      @Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    PaymentRefundProcessRequestDto paymentRequestDto = objectMapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-group", topics = "payment-refund-ticket-topic")
  public void consumePaymentRefundFromTicket(
      @Payload ApiResponse<PaymentRefundFromTicketRequestDto> message) {

    PaymentRefundFromTicketRequestDto paymentRefundRequestDto = objectMapper.convertValue(
        message.getData(),
        PaymentRefundFromTicketRequestDto.class);

    paymentService.refundPaymentFromTicket(paymentRefundRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-fail-group", topics = "payment-refund-fail-topic")
  public void consumePaymentRefundFail(
      @Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    PaymentRefundProcessRequestDto paymentRequestDto = objectMapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentRefundFail(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-ticket-group", topics = "payment-refund-ticket-success-process-topic")
  public void consumePaymentRefundTicketProcess(
      @Payload ApiResponse<ProcessTicketPaymentRequestDto> message) {

    ProcessTicketPaymentRequestDto paymentRequestDto = objectMapper.convertValue(message.getData(),
        ProcessTicketPaymentRequestDto.class);

    paymentService.processTicketPaymentRefundSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-status-update-refund-group", topics = "payment-status-update-refund-topic")
  public void consumePaymentStatusUpdateRefund(
      @Payload ApiResponse<PaymentStatusUpdateRefundRequestDto> message) {

    PaymentStatusUpdateRefundRequestDto requestDto = objectMapper.convertValue(message.getData(),
        PaymentStatusUpdateRefundRequestDto.class);

    paymentService.paymentStatusUpdateRefund(requestDto);
  }
}
