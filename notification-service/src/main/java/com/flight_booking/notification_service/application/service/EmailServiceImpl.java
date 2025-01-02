package com.flight_booking.notification_service.application.service;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final MailSender mailSender; // MailSender 인터페이스를 통한 의존성 주입
  private final NotificationRepository repository;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendNotification(Notification notification) {
    // 메일 전송
    boolean emailSent = mailSender.send(
        notification.getReceiverEmail(),
        notification.getTitle(),
        notification.getContent()
    );

    // 알림 상태 업데이트
    notification.setSent(emailSent);
    notification.setSentAt(emailSent ? LocalDateTime.now() : null);
    notification.setStatus(emailSent ? "SUCCESS" : "FAIL");
    if (!emailSent) {
      notification.setErrorMessage("Failed to send email");
    }

    // 데이터 저장
    repository.save(notification);
  }
}