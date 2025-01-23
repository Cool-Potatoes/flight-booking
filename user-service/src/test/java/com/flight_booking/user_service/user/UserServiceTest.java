package com.flight_booking.user_service.user;

import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.user_service.application.service.UserService;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import java.lang.reflect.Field;
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

    UserRequestDto requestDto1 = new UserRequestDto(
        ticketId, userEmail, 1000L, UUID.randomUUID()
    );
    UserRequestDto requestDto2 = new UserRequestDto(
        ticketId, userEmail, 2000L, UUID.randomUUID()
    );

//    스레드1에서 뉴 스레드 하고
//        로직을 하는데
//            그 안에 첫줄게 findbyemailwithlock을 해서 객체를 영속성으로 가져옴
//        만약에 스레드 2도 같은로직이면 동시에 가져오거나 / 락이 걸려있으면 한쪽만 가져온다
////        스레드 sleep 줘서 둘다 충분히 접근시간을 주고 업데이트
//        db에 flush하고 comit 하면 정상 금액이 아니라 다른금액이 들어갈것임
//
//        db락이 적용되었을떄 1개
//      락 없을 때 1개
//      이렇게 2개 테스트 진행



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
}
