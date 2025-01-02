package com.flight_booking.notification_service;

import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import com.flight_booking.notification_service.global.exception.NotificationNotFoundException;
import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import com.flight_booking.notification_service.application.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

  @InjectMocks
  private NotificationService notificationService;

  @Mock
  private NotificationRepository repository;

  @BeforeEach
  void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateNotification() {
    // 알림 생성 테스트
    NotificationRequest request = new NotificationRequest(
        1L, 2L, "TICKET_PURCHASE", "test@example.com", "Ticket Purchased", "Your ticket has been purchased."
    );

    Notification mockNotification = Notification.builder()
        .notificationId(UUID.randomUUID())
        .ticketId(request.ticketId())
        .userId(request.userId())
        .notificationType(request.notificationType())
        .receiverEmail(request.receiverEmail())
        .title(request.title())
        .content(request.content())
        .build();

    when(repository.save(any(Notification.class))).thenReturn(mockNotification);

    // 서비스 호출 및 검증
    NotificationResponse response = notificationService.createNotification(request);

    assertNotNull(response, "응답이 null입니다.");
    assertEquals(request.notificationType(), response.notificationType(), "알림 타입이 일치하지 않습니다.");
    verify(repository, times(1)).save(any(Notification.class));
  }

  @Test
  void testGetNotificationById_Success() {
    // 알림 ID로 조회 성공 테스트
    UUID notificationId = UUID.randomUUID();
    Notification mockNotification = Notification.builder()
        .notificationId(notificationId)
        .isRead(false)
        .build();

    when(repository.findById(notificationId)).thenReturn(Optional.of(mockNotification));

    // 서비스 호출 및 검증
    NotificationResponse response = notificationService.getNotificationById(notificationId);

    assertNotNull(response, "응답이 null입니다.");
    assertEquals(notificationId, response.notificationId(), "알림 ID가 일치하지 않습니다.");
  }

  @Test
  void testGetNotificationById_NotFound() {
    // 알림 ID로 조회 실패 테스트
    UUID notificationId = UUID.randomUUID();
    when(repository.findById(notificationId)).thenReturn(Optional.empty());

    // 예외 발생 검증
    assertThrows(NotificationNotFoundException.class, () -> notificationService.getNotificationById(notificationId));
  }

  @Test
  void testMarkAsRead_Success() {
    // 알림 읽음 처리 성공 테스트
    UUID notificationId = UUID.randomUUID();
    Notification mockNotification = Notification.builder()
        .notificationId(notificationId)
        .isRead(false)
        .build();

    when(repository.findById(notificationId)).thenReturn(Optional.of(mockNotification));

    // 서비스 호출 및 검증
    NotificationResponse response = notificationService.markAsRead(notificationId);

    assertNotNull(response, "응답이 null입니다.");
    assertTrue(response.isRead(), "알림이 읽음 상태가 아닙니다.");
    verify(repository, times(1)).save(mockNotification);
  }

  @Test
  void testGetNotificationsByUserIdWithPaging() {
    // 페이징된 알림 목록 조회 테스트
    Pageable pageable = PageRequest.of(0, 10);

    Notification notification = Notification.builder()
        .userId(1L)
        .ticketId(12345L)
        .notificationType("INFO")
        .receiverEmail("test@example.com")
        .isRead(false)
        .build();

    // Mock 페이징 데이터 생성
    Page<Notification> mockPage = new PageImpl<>(List.of(notification), pageable, 1);

    when(repository.findByUserIdAndIsDeletedFalse(1L, pageable)).thenReturn(mockPage);

    // 서비스 호출 및 검증
    Page<NotificationResponse> responsePage = notificationService.getNotificationsByUserId(1L, pageable);

    assertEquals(1, responsePage.getContent().size(), "페이징된 결과의 크기가 일치하지 않습니다.");
    assertEquals(1L, responsePage.getContent().get(0).userId(), "사용자 ID가 일치하지 않습니다.");
    verify(repository, times(1)).findByUserIdAndIsDeletedFalse(1L, pageable);
  }
}
