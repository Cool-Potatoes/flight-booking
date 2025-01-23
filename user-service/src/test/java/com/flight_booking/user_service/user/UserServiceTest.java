package com.flight_booking.user_service.user;

import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);


  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  // 락 적용 테스트
  @Test
  @Transactional
  public void testPessimisticLockingOnUserMileage() throws InterruptedException {
    // 초기 데이터 설정
    logger.info("초기 사용자 데이터를 설정합니다.");
    UUID ticketId = UUID.randomUUID();

    User user = User.builder()
        .email("testuser@example.com")
        .password("password123")
        .name("Test User")
        .phone("123456789")
        .mileage(3000L) // 초기 마일리지 설정
        .role(Role.USER)
        .isBlocked(false)
        .build();

    User savedUser = userRepository.save(user);
    String userEmail = savedUser.getEmail();

    Thread thread1 = new Thread(() -> {
      try {

        User lockUser = userRepository.findByEmailWithLock(userEmail)
            .orElseThrow();
        Thread.sleep(2000);
        lockUser.updateMile(1500L);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Thread thread2 = new Thread(() -> {
      try {

        User lockUser = userRepository.findByEmailWithLock(userEmail)
            .orElseThrow();
        Thread.sleep(2000);
        lockUser.updateMile(500L);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    // 두 스레드를 동시에 실행
    thread1.start();
    thread2.start();

    // 두 스레드가 종료될 때까지 대기
    thread1.join();
    thread2.join();

    // 최종 데이터 확인
    User resultUser = userRepository.findByEmail(userEmail).orElseThrow();
    Assertions.assertEquals(1000, resultUser.getMileage(), "최종 마일리지가 일치합니다");
  }

  // 락 미적용 테스트
  @Test
  @Transactional
  public void testNonLockingUpdateUserMileage() throws InterruptedException {
    // 초기 데이터 설정
    logger.info("초기 사용자 데이터를 설정합니다.");
    UUID ticketId = UUID.randomUUID();

    User user = User.builder()
        .email("testuser@example.com")
        .password("password123")
        .name("Test User")
        .phone("123456789")
        .mileage(3000L) // 초기 마일리지 설정
        .role(Role.USER)
        .isBlocked(false)
        .build();

    User savedUser = userRepository.save(user);
    String userEmail = savedUser.getEmail();

    Thread thread1 = new Thread(() -> {
      try {

        User lockUser = userRepository.findByEmail(userEmail)
            .orElseThrow();
        Thread.sleep(2000);
        lockUser.updateMile(1500L);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Thread thread2 = new Thread(() -> {
      try {

        User lockUser = userRepository.findByEmailWithLock(userEmail)
            .orElseThrow();
        Thread.sleep(2000);
        lockUser.updateMile(500L);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    // 두 스레드를 동시에 실행
    thread1.start();
    thread2.start();

    // 두 스레드가 종료될 때까지 대기
    thread1.join();
    thread2.join();

    // 최종 데이터 확인
    User resultUser = userRepository.findByEmail(userEmail).orElseThrow();
    Assertions.assertEquals(1000, resultUser.getMileage(), "최종 마일리지가 일치합니다");
  }
}
