package com.flight_booking.notification_service.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
    UUID notificationId,
    Long userId,
    String notificationType,
    String title,
    String content,
    boolean isRead,
    boolean isSent,
    LocalDateTime createdAt,
    LocalDateTime sentAt,
    String status,
    String errorMessage
) {}

