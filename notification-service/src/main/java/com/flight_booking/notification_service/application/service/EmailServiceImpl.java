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

  private final MailSender mailSender; // 이메일 전송 의존성
  private final NotificationRepository repository; // 알림 데이터 저장소

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendNotification(Notification notification) {
    // 1. 이메일 전송 시도
    boolean emailSent = mailSender.send(
        notification.getReceiverEmail(),
        notification.getTitle(),
        notification.getContent()
    );

    // 2. 이메일 전송 상태 업데이트
    notification.updateStatus(emailSent);

    // 3. 알림 객체 저장 (DB 업데이트)
    repository.save(notification);
  }
}
