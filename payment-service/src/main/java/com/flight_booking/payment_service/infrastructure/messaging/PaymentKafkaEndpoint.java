package com.flight_booking.payment_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRefundFromTicketRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundProcessRequestDto;
import com.flight_booking.common.application.dto.ProcessTicketPaymentRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentKafkaEndpoint {

  private final PaymentService paymentService;

  @KafkaListener(groupId = "payment-service-group", topics = "payment-creation-topic")
  public void consumePaymentCreation(@Payload ApiResponse<PaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        PaymentRequestDto.class);

    paymentService.createPayment(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-process-group", topics = "payment-success-process-topic")
  public void consumePaymentSuccessProcess(@Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundProcessRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-fail-group", topics = "payment-fail-process-topic")
  public void consumePaymentFailProcess(@Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundProcessRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentFail(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-group", topics = "payment-refund-topic")
  public void consumePaymentRefund(
      @Payload ApiResponse<PaymentRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundRequestDto paymentRefundRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundRequestDto.class);

    paymentService.sendPaymentRefundFair(paymentRefundRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-group", topics = "payment-refund-ticket-topic")
  public void consumePaymentRefundFromTicket(
      @Payload ApiResponse<PaymentRefundFromTicketRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundFromTicketRequestDto paymentRefundRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundFromTicketRequestDto.class);

    paymentService.refundPaymentFromTicket(paymentRefundRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-success-group", topics = "payment-refund-success-topic")
  public void consumePaymentRefundSuccess(
      @Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundProcessRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentRefundSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-fail-group", topics = "payment-refund-fail-topic")
  public void consumePaymentRefundFail(
      @Payload ApiResponse<PaymentRefundProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundProcessRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundProcessRequestDto.class);

    paymentService.processPaymentRefundFail(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-ticket-group", topics = "payment-refund-ticket-success-process-topic")
  public void consumePaymentRefundTicketProcess(
      @Payload ApiResponse<ProcessTicketPaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    ProcessTicketPaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        ProcessTicketPaymentRequestDto.class);

    paymentService.processTicketPaymentRefundSuccess(paymentRequestDto);
  }


}
