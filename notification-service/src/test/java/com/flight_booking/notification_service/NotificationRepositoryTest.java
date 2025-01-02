package com.flight_booking.notification_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    // 테스트 전 데이터 초기화
    notificationRepository.deleteAll();
  }

  @Test
  void testSaveAndFindNotification() {
    // 알림 저장 및 조회 테스트
    Notification notification = Notification.builder()
        .ticketId(1L)
        .userId(1L)
        .notificationType("INFO")
        .receiverEmail("test@example.com")
        .title("Test Notification")
        .content("This is a test notification.")
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    // 저장
    Notification savedNotification = notificationRepository.save(notification);

    // 조회 검증
    Optional<Notification> retrievedNotification = notificationRepository.findById(savedNotification.getNotificationId());
    assertTrue(retrievedNotification.isPresent(), "알림이 저장되지 않았습니다.");
    assertEquals("INFO", retrievedNotification.get().getNotificationType(), "알림 타입이 일치하지 않습니다.");
  }

  @Test
  void testFindByUserIdAndIsDeletedFalseWithPaging() {
    // 페이징된 알림 목록 조회 테스트
    notificationRepository.save(
        Notification.builder()
            .userId(1L)
            .ticketId(12345L)
            .notificationType("INFO")
            .receiverEmail("test@example.com")
            .isRead(false)
            .isSent(false)
            .status("PENDING")
            .build()
    );

    Pageable pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개 크기 요청
    Page<Notification> page = notificationRepository.findByUserIdAndIsDeletedFalse(1L, pageable);

    // 페이징 데이터 검증
    assertEquals(1, page.getContent().size(), "알림 개수가 일치하지 않습니다.");
    assertEquals(1L, page.getContent().get(0).getUserId(), "사용자 ID가 일치하지 않습니다.");
  }
}
