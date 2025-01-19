package com.flight_booking.payment_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.infrastructure.scheduling.PaymentScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@RequiredArgsConstructor
@Slf4j
public class PaymentRetryWorker {

  private final PaymentKafkaSender paymentKafkaSender;
  private final PaymentScheduler paymentScheduler;
  private final PaymentService paymentService;

  private final int MAX_RETRY_COUNT = 5;
  private final int[] delays = {3, 5, 7, 9, 10}; // minutes

  @KafkaListener(groupId = "payment-retry-group", topics = "payment-retry-topic")
  public void processRetry(@Payload ApiResponse<PaymentRetryRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PaymentRetryRequestDto requestDto
        = mapper.convertValue(message.getData(), PaymentRetryRequestDto.class);

    int retryCount = requestDto.retryCount();

    if (retryCount == 0) {
      long delay = (long) delays[0] * 1000 * 60;
      retryCount++;

      schedulePaymentRetry(requestDto, retryCount, delay);

      return;
    }

    if (retryCount >= MAX_RETRY_COUNT) {
      // TODO 예매 취소 로직
      // TODO 유저에게 알림 발송
      paymentKafkaSender.sendMessage(
          "payment-dlq-topic", requestDto.email(), requestDto,
          StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());
      return;
    }

    try {
      // 결제 재시도
      boolean success = retryPayment(requestDto);
      if (success) {
        log.info("Payment Retry Success, BookingId: " + requestDto.bookingId());
        // TODO 유저에게 알림 발솔
      } else {
        throw new RuntimeException("Payment failed");
      }
    } catch (RuntimeException e) {
      log.error("Payment Retry failed, BookingId: " + requestDto.bookingId()
          + " , Retry Count: " + retryCount);
      long delay = (long) delays[retryCount] * 1000 * 60;
      retryCount++;
      schedulePaymentRetry(requestDto, retryCount, delay);
    }

  }


  private void schedulePaymentRetry(PaymentRetryRequestDto requestDto, int retryCount, long delay) {
    PaymentRetryRequestDto updatedRequestDto
        = PaymentRetryRequestDto.from(requestDto, retryCount);
    paymentScheduler.scheduleRetryWithDelay(delay, updatedRequestDto);
  }

  private boolean retryPayment(PaymentRetryRequestDto requestDto) {
    // 재결제 메서드 호출 TODO
//    boolean success = paymentService.retryPayment(requestDto);

    return Math.random() > 0.5;
  }


}
