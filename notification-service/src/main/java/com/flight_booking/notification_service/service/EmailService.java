package com.flight_booking.notification_service.service;

import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 이메일 전송 서비스
 * 이메일 전송과 전송 결과에 따라 알림 상태를 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  @Value("${email.smtp.host}")
  private String smtpHost;

  @Value("${email.smtp.port}")
  private int smtpPort;

  @Value("${email.smtp.username}")
  private String smtpUsername;

  @Value("${email.smtp.password}")
  private String smtpPassword;

  private final NotificationRepository repository;

  /**
   * 이메일 전송 메서드
   * 이메일 전송 성공 여부에 따라 알림 상태 업데이트
   *
   * @param notification 알림 객체
   */
  public void sendNotification(Notification notification) {
    boolean emailSent = sendEmail(
        notification.getReceiverEmail(),
        notification.getTitle(),
        notification.getContent()
    );

    // 이메일 전송 결과에 따른 상태 업데이트
    if (emailSent) {
      notification.setSent(true);
      notification.setSentAt(LocalDateTime.now());
      notification.setStatus("SUCCESS");
    } else {
      notification.setStatus("FAIL");
      notification.setErrorMessage("Failed to send email");
    }

    repository.save(notification);
  }

  /**
   * 실제 이메일 전송 로직
   *
   * @param to 수신자 이메일
   * @param subject 이메일 제목
   * @param content 이메일 내용
   * @return 이메일 전송 성공 여부
   */
  private boolean sendEmail(String to, String subject, String content) {
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
