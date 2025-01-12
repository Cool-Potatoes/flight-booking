package com.flight_booking.notification_service.infrastructure.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

// SMTP 서버를 통해 메일을 전송하는 구현체
@Service
public class SmtpMailSender implements MailSender {

  @Value("${spring.mail.host}")
  private String smtpHost;

  @Value("${spring.mail.port}")
  private int smtpPort;

  @Value("${spring.mail.username}")
  private String smtpUsername;

  @Value("${spring.mail.password}")
  private String smtpPassword;

  @Override
  public boolean send(String to, String subject, String content) {
    // 1. SMTP 설정
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    try {
      // 2. SMTP 세션 생성
      Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(smtpUsername, smtpPassword);
        }
      });

      // 3. 이메일 생성
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUsername));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8");

      // 4. 이메일 전송
      Transport.send(message);
      return true;
    } catch (MessagingException e) {
      return false; // 이메일 전송 실패
    }
  }
}
