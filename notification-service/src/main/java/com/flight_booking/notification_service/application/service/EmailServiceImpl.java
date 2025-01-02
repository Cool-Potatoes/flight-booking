package com.flight_booking.notification_service.application.service;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 이메일 전송 서비스 구현체
 * 이메일 전송과 상태 업데이트를 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  @Value("${spring.mail.host}")
  private String smtpHost;

  @Value("${spring.mail.port}")
  private int smtpPort;

  @Value("${spring.mail.username}")
  private String smtpUsername;

  @Value("${spring.mail.password}")
  private String smtpPassword;

  private final NotificationRepository repository;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendNotification(Notification notification) {
    boolean emailSent = sendEmail(
        notification.getReceiverEmail(),
        notification.getTitle(),
        notification.getContent()
    );

    // 상태 업데이트
    notification.setSent(emailSent);
    notification.setSentAt(emailSent ? LocalDateTime.now() : null);
    notification.setStatus(emailSent ? "SUCCESS" : "FAIL");
    if (!emailSent) {
      notification.setErrorMessage("Failed to send email");
    }

    repository.save(notification);
  }

  public boolean sendEmail(String to, String subject, String content) {
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(smtpUsername, smtpPassword);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUsername));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8");

      Transport.send(message);
      log.info("Email sent successfully to {}", to);
      return true;
    } catch (MessagingException e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
      return false;
    }
  }
}
