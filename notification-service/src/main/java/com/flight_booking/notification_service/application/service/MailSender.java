package com.flight_booking.notification_service.application.service;

public interface MailSender {
  boolean send(String to, String subject, String content);
}
