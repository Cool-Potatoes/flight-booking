package com.flight_booking.notification_service.service;

import com.flight_booking.notification_service.dto.NotificationRequest;
import com.flight_booking.notification_service.dto.NotificationResponse;
import com.flight_booking.notification_service.exception.NotificationNotFoundException;
import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository repository;
  private final EmailService emailService;

  // 알림 생성 + 이메일 전송
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

    emailService.sendEmail(
        request.receiverEmail(),
        request.title(),
        request.content()
    );
    saved.setSent(true);
    saved.setSentAt(LocalDateTime.now());
    saved.setStatus("SUCCESS");

    repository.save(saved);
    return toResponse(saved);
  }

  // 단건 조회
  public NotificationResponse getNotificationById(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("알림을 찾을 수 없습니다. ID: " + id));
    return toResponse(notification);
  }

  // 사용자 알림 목록
  public List<NotificationResponse> getNotificationsByUserId(Long userId) {
    return repository.findByUserIdAndIsDeletedFalse(userId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // 알림 읽음 처리
  public NotificationResponse markAsRead(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(() -> new NotificationNotFoundException("알림을 찾을 수 없습니다. ID: " + id));
    notification.setRead(true);
    repository.save(notification);
    return toResponse(notification);
  }

  // 알림 소프트 삭제
  public void deleteNotification(UUID id) {
    repository.softDelete(id);
  }

  // Notification → NotificationResponse 변환
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
