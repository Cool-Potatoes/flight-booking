package com.flight_booking.notification_service.service;

import com.flight_booking.notification_service.dto.NotificationRequest;
import com.flight_booking.notification_service.dto.NotificationResponse;
import com.flight_booking.notification_service.exception.NotificationNotFoundException;
import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 알림 서비스
 * 알림 생성, 조회, 삭제, 읽음 처리 등 알림과 관련된 비즈니스 로직을 담당
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository repository;
  private final EmailService emailService;

  /**
   * 알림 생성 메서드
   * 이메일 전송 결과에 따라 상태 업데이트
   */
  public NotificationResponse createNotification(NotificationRequest request) {
    Notification notification = Notification.builder()
        .ticketId(request.ticketId())
        .userId(request.userId())
        .notificationType(request.notificationType())
        .receiverEmail(request.receiverEmail())
        .title(request.title())
        .content(request.content())
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    Notification saved = repository.save(notification);

    // 이메일 전송 요청 (결과는 EmailService에서 처리)
    emailService.sendNotification(notification);

    return toResponse(saved);
  }

  public NotificationResponse getNotificationById(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("알림을 찾을 수 없습니다. ID: " + id));
    return toResponse(notification);
  }

  public List<NotificationResponse> getNotificationsByUserId(Long userId) {
    return repository.findByUserIdAndIsDeletedFalse(userId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public NotificationResponse markAsRead(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("알림을 찾을 수 없습니다. ID: " + id));
    notification.setRead(true);
    repository.save(notification);
    return toResponse(notification);
  }

  public void deleteNotification(UUID id) {
    repository.softDelete(id);
  }

  private NotificationResponse toResponse(Notification n) {
    return new NotificationResponse(
        n.getNotificationId(),
        n.getUserId(),
        n.getNotificationType(),
        n.getTitle(),
        n.getContent(),
        n.isRead(),
        n.isSent(),
        n.getCreatedAt(),
        n.getSentAt(),
        n.getStatus(),
        n.getErrorMessage()
    );
  }
}
