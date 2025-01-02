package com.flight_booking.notification_service.application.service;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  // SMTP 설정 정보
  @Value("${spring.mail.host}")
  private String smtpHost;

  @Value("${spring.mail.port}")
  private int smtpPort;

  @Value("${spring.mail.username}")
  private String smtpUsername;

  @Value("${spring.mail.password}")
  private String smtpPassword;

  // 알림 저장소
  private final NotificationRepository repository;

  // 이메일 전송 (비동기로 처리)
  @Async
  @Override
  public void sendNotification(Notification notification) {
    // 이메일 전송 성공 여부 확인
    boolean emailSent = sendEmail(notification.getReceiverEmail(), notification.getTitle(), notification.getContent());

    // 전송 결과에 따라 알림 상태 업데이트
    notification.setSent(emailSent);
    notification.setSentAt(emailSent ? LocalDateTime.now() : null);
    notification.setStatus(emailSent ? "SUCCESS" : "FAIL");
    if (!emailSent) {
      notification.setErrorMessage("Failed to send email");
    }

    // 업데이트된 알림 저장
    repository.save(notification);
  }

  // 실제 이메일 전송 로직
  private boolean sendEmail(String to, String subject, String content) {
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    try {
      // 이메일 세션 생성
      Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(smtpUsername, smtpPassword);
        }
      });

      // 메시지 구성
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUsername));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8");

      // 이메일 전송
      Transport.send(message);
      log.info("Email sent successfully to {}", to);
      return true;
    } catch (MessagingException e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
      return false;
    }
  }
}
