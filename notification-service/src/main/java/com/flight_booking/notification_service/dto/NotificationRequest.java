package com.flight_booking.notification_service.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @NotNull(message = "티켓 ID는 필수입니다.") Long ticketId,
    @NotNull(message = "사용자 ID는 필수입니다.") Long userId,
    @NotNull(message = "알림 유형은 필수입니다.") String notificationType,
    @NotNull(message = "수신자 이메일은 필수입니다.") String receiverEmail,
    String title,
    String content
) {}

