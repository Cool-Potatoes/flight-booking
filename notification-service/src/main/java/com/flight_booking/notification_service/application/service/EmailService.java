package com.flight_booking.notification_service.application.service;

import com.flight_booking.notification_service.domain.model.Notification;

/**
 * 이메일 전송 서비스 인터페이스
 * 이메일 전송과 관련된 동작 정의
 */
public interface EmailService {
  /**
   * 알림 객체를 기반으로 이메일을 전송
   *
   * @param notification 알림 객체
   */
  void sendNotification(Notification notification);
}
