package com.flight_booking.payment_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRefundFromTicketRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.ProcessPaymentRequestDto;
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
  public void consumePaymentSuccessProcess(@Payload ApiResponse<ProcessPaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    ProcessPaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        ProcessPaymentRequestDto.class);

    paymentService.processPaymentSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-fail-group", topics = "payment-fail-process-topic")
  public void consumePaymentFailProcess(@Payload ApiResponse<ProcessPaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    ProcessPaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        ProcessPaymentRequestDto.class);

    paymentService.processPaymentFail(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-group", topics = "payment-refund-topic")
  public void consumePaymentRefund(
      @Payload ApiResponse<PaymentRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundRequestDto paymentRefundRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundRequestDto.class);

    paymentService.refundPayment(paymentRefundRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-group", topics = "payment-refund-ticket-topic")
  public void consumePaymentRefundFromTicket(
      @Payload ApiResponse<PaymentRefundFromTicketRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRefundFromTicketRequestDto paymentRefundRequestDto = mapper.convertValue(message.getData(),
        PaymentRefundFromTicketRequestDto.class);

    paymentService.refundPaymentFromTicket(paymentRefundRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-success-group", topics = "payment-refund-success-process-topic")
  public void consumePaymentRefundFailProcess(
      @Payload ApiResponse<ProcessPaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    ProcessPaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        ProcessPaymentRequestDto.class);

    paymentService.processPaymentRefundSuccess(paymentRequestDto);
  }

  @KafkaListener(groupId = "payment-refund-fail-group", topics = "payment-refund-fail-process-topic")
  public void consumePaymentRefundSuccessProcess(
      @Payload ApiResponse<ProcessPaymentRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    ProcessPaymentRequestDto paymentRequestDto = mapper.convertValue(message.getData(),
        ProcessPaymentRequestDto.class);

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
