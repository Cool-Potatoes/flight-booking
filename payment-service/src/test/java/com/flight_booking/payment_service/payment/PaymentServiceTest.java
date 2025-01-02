package com.flight_booking.payment_service.payment;

import com.flight_booking.payment_service.application.service.PaymentService;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    PaymentRequestDto paymentRequestDto1 = new PaymentRequestDto(bookingId, 2000);
    PaymentRequestDto paymentRequestDto2 = new PaymentRequestDto(bookingId, 3000);

    // 첫 번째 트랜잭션: 결제 금액을 2000으로 업데이트
    Thread thread1 = new Thread(() -> {
      try {
        logger.info("스레드 1: 결제 금액 업데이트를 시도합니다.");
        paymentService.updateFare(paymentRequestDto1, paymentId);
        logger.info("스레드 1: 결제 금액 업데이트 완료.");
      } catch (Exception e) {
        logger.error("스레드 1에서 예외 발생: ", e);
      }
    });

    // 두 번째 트랜잭션: 결제 금액을 3000으로 업데이트
    Thread thread2 = new Thread(() -> {
      try {
        logger.info("스레드 2: 결제 금액 업데이트를 시도합니다.");
        paymentService.updateFare(paymentRequestDto2, paymentId);
        logger.info("스레드 2: 결제 금액 업데이트 완료.");
      } catch (Exception e) {
        logger.error("스레드 2에서 예외 발생: ", e);
      }
    });

    // 두 스레드를 동시에 실행
    thread2.start();
    thread1.start();

    // 두 스레드가 종료될 때까지 대기
    thread1.join();
    thread2.join();

    // 최종 결과를 확인합니다.
    PaymentResponseDto result = paymentService.getPayment(paymentId);
    logger.info("최종 결제 금액: {}", result.fare());
    assert (result.fare() == 3000 || result.fare() == 2000);
  }


}
