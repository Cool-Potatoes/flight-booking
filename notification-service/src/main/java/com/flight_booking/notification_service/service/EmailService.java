package com.flight_booking.notification_service.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

  @Value("${email.smtp.host}")
  private String smtpHost;

  @Value("${email.smtp.port}")
  private int smtpPort;

  @Value("${email.smtp.username}")
  private String smtpUsername;

  @Value("${email.smtp.password}")
  private String smtpPassword;

  public void sendEmail(String to, String subject, String content) {
    // SMTP 서버 설정
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    // Gmail SMTP 인증
    Session session = Session.getInstance(props,
        new jakarta.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(smtpUsername, smtpPassword);
          }
        });

    try {
      // 메시지 작성
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUsername));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8"); // HTML 지원

      // 이메일 전송
      Transport.send(message);
      System.out.println("이메일 전송 성공");
    } catch (MessagingException e) {
      System.err.println("이메일 전송 실패: " + e.getMessage());
      throw new RuntimeException("이메일 전송 실패", e);
    }
  }
}
