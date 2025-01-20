package com.flight_booking.payment_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.PaymentRefundProcessRequestDto;
import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.infrastructure.scheduling.PaymentScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class PaymentRetryWorker {

  private final PaymentScheduler paymentScheduler;
  private final PaymentService paymentService;

  private final int MAX_RETRY_COUNT = 5;
  private final int[] delays = {3, 2, 2, 2, 1}; // minutes

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
      paymentService.updatePaymentState(PaymentStatusEnum.IN_RETRY, requestDto.paymentId());

      return;
    }

    try {
      // 결제 재시도
      boolean success = retryPayment(requestDto);
      if (success) {
        log.info("Payment Retry Success, PaymentId: " + requestDto.paymentId());
        paymentService.sendPayedMessage(requestDto);
      } else {

        if (retryCount >= MAX_RETRY_COUNT) {  // 예매 취소
          paymentService.processPaymentFail(PaymentRefundProcessRequestDto.from(requestDto));
          paymentService.sendPaymentDLQ(requestDto);  // 결제를 DLQ로 전송
          return;
        }

        throw new RuntimeException("Payment failed");
      }
    } catch (RuntimeException e) {
      log.error("Payment Retry failed, PaymentId: " + requestDto.paymentId()
          + " , Retry Count: " + retryCount);
      long delay = (long) delays[retryCount - 1] * 1000 * 60;
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
    return paymentService.retryPayment(requestDto);
  }


}
