package com.flight_booking.notification_service;

import com.flight_booking.notification_service.dto.NotificationRequest;
import com.flight_booking.notification_service.dto.NotificationResponse;
import com.flight_booking.notification_service.exception.NotificationNotFoundException;
import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import com.flight_booking.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateNotification() {
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

    NotificationResponse response = notificationService.createNotification(request);

    assertNotNull(response);
    assertEquals(request.notificationType(), response.notificationType());
    verify(repository, times(1)).save(any(Notification.class));
  }

  @Test
  void testGetNotificationById_Success() {
    UUID notificationId = UUID.randomUUID();
    Notification mockNotification = Notification.builder()
        .notificationId(notificationId)
        .isRead(false)
        .build();

    when(repository.findById(notificationId)).thenReturn(Optional.of(mockNotification));

    NotificationResponse response = notificationService.getNotificationById(notificationId);

    assertNotNull(response);
    assertEquals(notificationId, response.notificationId());
  }

  @Test
  void testGetNotificationById_NotFound() {
    UUID notificationId = UUID.randomUUID();
    when(repository.findById(notificationId)).thenReturn(Optional.empty());

    assertThrows(NotificationNotFoundException.class, () -> notificationService.getNotificationById(notificationId));
  }

  @Test
  void testMarkAsRead_Success() {
    UUID notificationId = UUID.randomUUID();
    Notification mockNotification = Notification.builder()
        .notificationId(notificationId)
        .isRead(false)
        .build();

    when(repository.findById(notificationId)).thenReturn(Optional.of(mockNotification));

    NotificationResponse response = notificationService.markAsRead(notificationId);

    assertNotNull(response);
    assertTrue(response.isRead());
    verify(repository, times(1)).save(mockNotification);
  }
}
