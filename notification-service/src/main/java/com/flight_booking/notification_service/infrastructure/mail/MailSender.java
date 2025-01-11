package com.flight_booking.notification_service.infrastructure.mail;

// 이메일 전송용 인터페이스
public interface MailSender {
  boolean send(String to, String subject, String content);
}