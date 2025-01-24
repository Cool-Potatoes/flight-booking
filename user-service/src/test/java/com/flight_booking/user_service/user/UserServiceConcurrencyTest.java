package com.flight_booking.user_service.user;

import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.messaging.UserKafkaSender;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.config.enabled=false"
})
@ActiveProfiles("test")
public class UserServiceConcurrencyTest {


  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Mock
  private UserKafkaSender userKafkaSender; // Kafka 메시지 전송 Mock

  @Test
  public void testUpdateUserMileageConcurrency() throws InterruptedException {
    // 초기 데이터 설정
    UUID ticketId = UUID.randomUUID();
    UUID paymentId1 = UUID.randomUUID();
    UUID paymentId2 = UUID.randomUUID();

    User user = User.builder()
        .email("testuser@example.com")
        .password("password123")
        .name("Test User")
        .phone("123456789")
        .mileage(10000L) // 초기 마일리지 설정
        .role(Role.USER)
        .isBlocked(false)
        .build();

    userRepository.save(user);

    UserRequestDto requestDto1 = new UserRequestDto(ticketId, user.getEmail(), 1500L, paymentId1);
    UserRequestDto requestDto2 = new UserRequestDto(ticketId, user.getEmail(), 2000L, paymentId2);

    // 동시성 테스트를 위한 스레드 풀과 카운트다운 래치 설정
    int threadCount = 2;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호 대기
    CountDownLatch endLatch = new CountDownLatch(threadCount); // 작업 종료 대기

    // 두 개의 스레드를 생성하여 동시에 요청
    executorService.submit(() -> {
      try {
        startLatch.await(); // 시작 신호를 기다림
        boolean result = userService.updateUserMileage(requestDto1);
        System.out.println("Thread 1 result: " + result);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        endLatch.countDown();
      }
    });

    executorService.submit(() -> {
      try {
        startLatch.await(); // 시작 신호를 기다림
        boolean result = userService.updateUserMileage(requestDto2);
        System.out.println("Thread 2 result: " + result);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        endLatch.countDown();
      }
    });

    // 두 스레드가 준비되면 동시에 시작
    startLatch.countDown();

    // 모든 스레드가 작업을 완료할 때까지 대기
    endLatch.await();

    // 최종 마일리지 검증
    User resultUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
    Assertions.assertEquals(6500, resultUser.getMileage(), "최종 마일리지가 일치하지 않습니다.");
  }
}