package com.flight_booking.payment_service.payment;

import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.request.UpdateFareRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

  @Autowired
  private PaymentService paymentService;

  @Test
  public void testPessimisticLocking() throws InterruptedException {
    // 초기 데이터 설정
    logger.info("초기 아이템 데이터를 설정합니다.");
    UUID bookingId = UUID.randomUUID();
    PaymentRequestDto paymentRequestDto = new PaymentRequestDto(bookingId, 1000);
    PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto);

    UUID paymentId = paymentResponseDto.paymentId();

    UpdateFareRequestDto updateFareRequestDto1
        = new UpdateFareRequestDto(bookingId, 1000, 2000);
    UpdateFareRequestDto updateFareRequestDto2
        = new UpdateFareRequestDto(bookingId, 1000, 3000);

    // 트랜잭션1: 결제 금액을 2000으로 업데이트 시도
    Thread thread1 = new Thread(() -> {
      try {
        logger.info("스레드 1: 결제 금액 업데이트를 시도합니다.");
        paymentService.updateFare(updateFareRequestDto1, paymentId);
        logger.info("스레드 1: 결제 금액 업데이트 완료.");
      } catch (CannotAcquireLockException e) {
        // 다른 트랜잭션이 결제 금액을 변경하여 기존 결제 금액과 다르면 예외 발생
        logger.error("스레드 1에서 예외 발생: ", e);
        PaymentResponseDto result = paymentService.getPayment(paymentId);
        logger.info("최종 결제 금액: {}", result.fare());
        Assertions.assertEquals(3000, result.fare());
      }
    });

    // 트랜잭션2: 결제 금액을 3000으로 업데이트 시도
    Thread thread2 = new Thread(() -> {
      try {
        logger.info("스레드 2: 결제 금액 업데이트를 시도합니다.");
        paymentService.updateFare(updateFareRequestDto2, paymentId);
        logger.info("스레드 2: 결제 금액 업데이트 완료.");
      } catch (CannotAcquireLockException e) {
        // 다른 트랜잭션이 결제 금액을 변경하여 기존 결제 금액과 다르면 예외 발생
        logger.error("스레드 2에서 예외 발생: ", e);
        PaymentResponseDto result = paymentService.getPayment(paymentId);
        logger.info("최종 결제 금액: {}", result.fare());
        Assertions.assertEquals(2000, result.fare());
      }
    });

    // 두 스레드를 동시에 실행
    thread2.start();
    thread1.start();

    // 두 스레드가 종료될 때까지 대기
    thread1.join();
    thread2.join();

  }


}
