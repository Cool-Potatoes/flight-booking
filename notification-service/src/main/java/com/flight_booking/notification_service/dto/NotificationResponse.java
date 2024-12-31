package com.flight_booking.notification_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

// 알림 응답 DTO
@Data
@Builder
public class NotificationResponse {

  private UUID notificationId;   // 알림 ID
  private Long userId;           // 사용자 ID
  private String notificationType;
  private String title;
  private String content;
  private boolean isRead;
  private boolean isSent;
  private LocalDateTime createdAt;
  private LocalDateTime sentAt;
  private String status;
  private String errorMessage;
}
