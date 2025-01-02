package com.flight_booking.notification_service.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

// SMTP를 통해 메일을 전송하는 구현체
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

  // SMTP를 사용하여 이메일 전송
  @Override
  public boolean send(String to, String subject, String content) {
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    try {
      Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(smtpUsername, smtpPassword);
        }
      });

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(smtpUsername));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8");

      Transport.send(message);
      return true;
    } catch (MessagingException e) {
      return false;
    }
  }
}
