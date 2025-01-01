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

  // [1] 알림 생성
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
    return toResponse(saved);
  }

  // [2] 특정 알림 조회
  public NotificationResponse getNotificationById(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));
    return toResponse(notification);
  }

  // [3] 사용자 알림 목록 조회
  public List<NotificationResponse> getNotificationsByUserId(Long userId) {
    return repository.findByUserIdAndIsDeletedFalse(userId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // [4] 알림 읽음 처리
  public NotificationResponse markAsRead(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));
    notification.setRead(true);
    repository.save(notification);
    return toResponse(notification);
  }

  // [5] 알림 삭제
  public void deleteNotification(UUID id) {
    repository.softDelete(id);
  }

  // Notification 엔티티 → NotificationResponse 변환
  private NotificationResponse toResponse(Notification notification) {
    return new NotificationResponse(
        notification.getNotificationId(),
        notification.getUserId(),
        notification.getNotificationType(),
        notification.getTitle(),
        notification.getContent(),
        notification.isRead(),
        notification.isSent(),
        notification.getCreatedAt(),
        notification.getSentAt(),
        notification.getStatus(),
        notification.getErrorMessage()
    );
  }
}
