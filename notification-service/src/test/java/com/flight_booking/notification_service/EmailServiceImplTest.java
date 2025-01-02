package com.flight_booking.notification_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.flight_booking.notification_service.application.service.EmailServiceImpl;
import com.flight_booking.notification_service.application.service.MailSender;
import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmailServiceImplTest {

  @Mock
  private NotificationRepository repository;

  @Mock
  private MailSender mailSender; // MailSender를 Mock으로 주입

  @InjectMocks
  private EmailServiceImpl emailService;

  @BeforeEach
  void setUp() {
    // Mockito 초기화
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSendNotification_Success() {
    // 성공적인 이메일 전송 테스트
    // Mock 데이터 생성
    Notification notification = Notification.builder()
        .receiverEmail("recipient@example.com")
        .title("Test Email")
        .content("This is a test email.")
        .build();

    // MailSender의 send 메서드를 Mock 처리하여 true 반환
    when(mailSender.send(anyString(), anyString(), anyString())).thenReturn(true);

    // 실행
    emailService.sendNotification(notification);

    // 저장 호출 검증
    verify(repository, times(1)).save(notification);
    // 이메일 전송 상태 검증
    assertTrue(notification.isSent());
    assertEquals("SUCCESS", notification.getStatus());
  }

  @Test
  void testSendNotification_Failure() {
    // 실패한 이메일 전송 테스트
    // Mock 데이터 생성
    Notification notification = Notification.builder()
        .receiverEmail("invalid-email")
        .title("Test Email")
        .content("This is a test email.")
        .build();

    // MailSender의 send 메서드를 Mock 처리하여 false 반환
    when(mailSender.send(anyString(), anyString(), anyString())).thenReturn(false);

    // 실행
    emailService.sendNotification(notification);

    // 저장 호출 검증
    verify(repository, times(1)).save(notification);
    // 이메일 전송 상태 검증
    assertFalse(notification.isSent());
    assertEquals("FAIL", notification.getStatus());
  }
}
