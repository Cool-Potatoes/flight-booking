package com.flight_booking.notification_service.application.service;

import com.flight_booking.notification_service.domain.model.Notification;

// 이메일 전송 서비스 인터페이스
public interface EmailService {

  // 알림 객체를 기반으로 이메일을 비동기로 전송
  void sendNotification(Notification notification);
}
