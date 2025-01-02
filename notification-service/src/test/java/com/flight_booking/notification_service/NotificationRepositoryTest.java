package com.flight_booking.notification_service;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    // 데이터 초기화
    notificationRepository.deleteAll();
  }

  @Test
  void testSaveAndFindNotification() {
    // [1] 새로운 알림 생성
    Notification notification = Notification.builder()
        .ticketId(1L)  // 필수 필드
        .userId(1L)
        .notificationType("INFO")
        .receiverEmail("test@example.com")
        .title("Test Notification")
        .content("This is a test notification.")
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .version(Long.valueOf(Integer.valueOf(0)))  // 버전 초기화
        .build();

    // [2] 저장
    Notification savedNotification = notificationRepository.save(notification);

    // [3] 저장된 데이터 검증
    Optional<Notification> retrievedNotification = notificationRepository.findById(savedNotification.getNotificationId());
    assertTrue(retrievedNotification.isPresent(), "알림이 저장되지 않았습니다.");
    assertEquals("INFO", retrievedNotification.get().getNotificationType(), "알림 타입이 일치하지 않습니다.");
    assertEquals(0, retrievedNotification.get().getVersion(), "버전이 초기화되지 않았습니다.");
  }
}
