package com.flight_booking.payment_service.infrastructure.scheduling;

import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.payment_service.infrastructure.messaging.PaymentKafkaSender;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentScheduler {

  @Value("${retry-payment.scheduler.thread-pool}")
  private int threadPool;

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPool);
  private final PaymentKafkaSender paymentKafkaSender;

  @PostConstruct
  public void init() {
    this.scheduler = Executors.newScheduledThreadPool(threadPool);
  }

  @RefreshScope
  @PostConstruct // @RefreshScope 와 결합하면 refresh 엔드포인트 호출시 다시 호출되어 해당 빈의 필드 값이 다시 주입됨.
  public void refreshScheduler() {
    this.scheduler.shutdown();  // 기존 스케줄러 종료
    this.scheduler = Executors.newScheduledThreadPool(threadPool);  // 새로운 스케줄러 생성
    log.info("Scheduler thread pool has been refreshed with new size: " + threadPool);
  }

  public void scheduleRetryWithDelay(long delay, PaymentRetryRequestDto requestDto) {
    log.info("Payment retry is scheduled, BookingId: " + requestDto.bookingId());

    scheduler.schedule(() -> {
      paymentKafkaSender.sendMessage(
          "payment-retry-topic", requestDto.bookingId().toString(), requestDto,
          StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName()
      );
    }, delay, TimeUnit.MILLISECONDS);
  }

}
