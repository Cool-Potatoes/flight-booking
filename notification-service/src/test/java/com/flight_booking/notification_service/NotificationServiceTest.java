package com.flight_booking.notification_service;

import com.flight_booking.notification_service.application.service.NotificationService;
import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

  @Mock
  private NotificationRepository repository;

  @InjectMocks
  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateNotification() {
    NotificationRequest request = new NotificationRequest(
        null, // ticketId 제거
        1L,
        "EMAIL",
        "test@example.com",
        "Test Title",
        "Test Content"
    );

    Notification notification = Notification.builder()
        .userId(request.userId())
        .notificationType(request.notificationType())
        .receiverEmail(request.receiverEmail())
        .title(request.title())
        .content(request.content())
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    when(repository.save(any(Notification.class))).thenReturn(notification);

    NotificationResponse response = notificationService.createNotification(request);

    assertNotNull(response);
    assertEquals(request.userId(), response.userId());
    assertEquals(request.notificationType(), response.notificationType());
  }

  @Test
  void testGetNotificationById() {
    UUID id = UUID.randomUUID();
    Notification notification = Notification.builder()
        .notificationId(id)
        .userId(1L)
        .notificationType("EMAIL")
        .title("Test Title")
        .content("Test Content")
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    when(repository.findById(id)).thenReturn(Optional.of(notification));

    NotificationResponse response = notificationService.getNotificationById(id);

    assertNotNull(response);
    assertEquals(id, response.notificationId());
    assertEquals(notification.getUserId(), response.userId());
  }

  @Test
  void testGetNotificationsByUserId() {
    long userId = 1L;
    Pageable pageable = Pageable.ofSize(10);
    Notification notification = Notification.builder()
        .userId(userId)
        .notificationType("EMAIL")
        .title("Test Title")
        .content("Test Content")
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    when(repository.findByUserIdAndIsDeletedFalse(userId, pageable))
        .thenReturn(new PageImpl<>(List.of(notification)));

    Page<NotificationResponse> response = notificationService.getNotificationsByUserId(userId, pageable);

    assertNotNull(response);
    assertEquals(1, response.getTotalElements());
  }

  @Test
  void testMarkAsRead() {
    UUID id = UUID.randomUUID();
    Notification notification = Notification.builder()
        .notificationId(id)
        .userId(1L)
        .notificationType("EMAIL")
        .title("Test Title")
        .content("Test Content")
        .isRead(false)
        .build();

    when(repository.findById(id)).thenReturn(Optional.of(notification));
    when(repository.save(notification)).thenReturn(notification);

    NotificationResponse response = notificationService.markAsRead(id);

    assertNotNull(response);
    assertTrue(response.isRead());
  }

  @Test
  void testDeleteNotification() {
    UUID id = UUID.randomUUID();

    doNothing().when(repository).softDelete(id);

    assertDoesNotThrow(() -> notificationService.deleteNotification(id));
    verify(repository, times(1)).softDelete(id);
  }
}
