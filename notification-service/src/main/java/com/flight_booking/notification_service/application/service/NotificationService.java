package com.flight_booking.notification_service.application.service;

import com.flight_booking.common.application.dto.NotificationRequestDto;
import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import com.flight_booking.notification_service.global.exception.NotificationNotFoundException;
import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  // 알림 저장소
  private final NotificationRepository repository;

  // 이메일 전송 서비스
  private final EmailService emailService;

  // 알림 생성
  public NotificationResponse createNotification(NotificationRequest request) {
    // 알림 엔티티 생성
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

    // 저장
    Notification saved = repository.save(notification);

    // 이메일 전송 (비동기)
    emailService.sendNotification(saved);

    // 응답 반환
    return toResponse(saved);
  }

  // 특정 알림 조회
  public NotificationResponse getNotificationById(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(
            () -> new NotificationNotFoundException("Notification not found with ID: " + id));
    return toResponse(notification);
  }

  // 사용자 ID에 따른 알림 목록 조회 (페이징)
  public Page<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable) {
    return repository.findByUserIdAndIsDeletedFalse(userId, pageable).map(this::toResponse);
  }

  // 알림 읽음 처리
  public NotificationResponse markAsRead(UUID id) {
    Notification notification = repository.findById(id)
        .filter(n -> !n.getIsDeleted())
        .orElseThrow(
            () -> new NotificationNotFoundException("Notification not found with ID: " + id));
    notification.setRead(true);
    repository.save(notification);
    return toResponse(notification);
  }

  // 알림 삭제 (소프트 삭제)
  public void deleteNotification(UUID id) {
    repository.softDelete(id);
  }

  // 알림 엔티티를 응답 객체로 변환
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

  // 비밀번호 변경을 위한 인증 코드 발송
  @Transactional
  public NotificationResponse sendCode(NotificationRequestDto requestDto) {
    log.info("Kafka 메시지 수신: {}", requestDto);

    // 알림 엔티티 생성
    Notification notification = Notification.builder()
        .userId(requestDto.userId())
        .receiverEmail(requestDto.receiverEmail())
        .notificationType("SEND_CODE")
        .title("비밀번호 변경을 위한 인증 코드 발급")
        .content("인증 코드: " + requestDto.code())
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    // 알림을 DB에 저장
    Notification saved = repository.save(notification);

    // 이메일 전송 (비동기 처리 가능)
    emailService.sendNotification(saved);

    log.info("Kafka 메시지 수신 완료");

    // 응답 반환
    return toResponse(saved);
  }
}
