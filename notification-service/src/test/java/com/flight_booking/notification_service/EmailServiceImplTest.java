package com.flight_booking.notification_service;

import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import com.flight_booking.notification_service.service.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

  @Mock
  private NotificationRepository repository;

  @InjectMocks
  private EmailServiceImpl emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSendNotification_Success() {
    // Mock 데이터 생성
    Notification notification = Notification.builder()
        .receiverEmail("recipient@example.com")
        .title("Test Email")
        .content("This is a test email.")
        .build();

    // Spy로 내부 메서드 Mock 설정
    EmailServiceImpl emailServiceSpy = spy(emailService);
    doReturn(true).when(emailServiceSpy).sendEmail(anyString(), anyString(), anyString());

    // 실행
    emailServiceSpy.sendNotification(notification);

    // 검증
    verify(repository, times(1)).save(notification);
    assertTrue(notification.isSent());
    assertEquals("SUCCESS", notification.getStatus());
  }

  @Test
  void testSendNotification_Failure() {
    // Mock 데이터 생성
    Notification notification = Notification.builder()
        .receiverEmail("invalid-email")
        .title("Test Email")
        .content("This is a test email.")
        .build();

    // Spy로 내부 메서드 Mock 설정
    EmailServiceImpl emailServiceSpy = spy(emailService);
    doReturn(false).when(emailServiceSpy).sendEmail(anyString(), anyString(), anyString());

    // 실행
    emailServiceSpy.sendNotification(notification);

    // 검증
    verify(repository, times(1)).save(notification);
    assertFalse(notification.isSent());
    assertEquals("FAIL", notification.getStatus());
  }
}
