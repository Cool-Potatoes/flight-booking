package com.flight_booking.notification_service.presentation.controller;

import com.flight_booking.common.application.dto.NotificationRequestDto;
import com.flight_booking.notification_service.application.service.NotificationService;
import com.flight_booking.notification_service.global.ApiResponse;
import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  // 알림 생성
  @PostMapping
  public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
      @RequestBody NotificationRequest request) {
    NotificationResponse response = notificationService.createNotification(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
  }

  // 특정 알림 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(@PathVariable UUID id) {
    NotificationResponse response = notificationService.getNotificationById(id);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 사용자 알림 목록 조회 (페이징)
  @GetMapping
  public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
      @RequestParam Long userId, Pageable pageable) {
    Page<NotificationResponse> response = notificationService.getNotificationsByUserId(userId,
        pageable);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 알림 읽음 처리
  @PatchMapping("/{id}/read")
  public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
    NotificationResponse response = notificationService.markAsRead(id);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  // 알림 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable UUID id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.ok(ApiResponse.ok("Notification deleted successfully."));
  }

  // 비밀번호 변경을 위한 인증 번호 전송
  @KafkaListener(topics = "password-reset-topic", groupId = "notification-group")
  public ResponseEntity<ApiResponse<NotificationResponse>> handleNotificationEvent(
      @Payload NotificationRequestDto notificationRequestDto) {

    log.info("Kafka 메시지 수신: {}", notificationRequestDto);

    NotificationResponse notificationResponse = notificationService.sendCode(
        notificationRequestDto);

    log.info("이메일 전송 완료: {}", notificationResponse);

    return ResponseEntity.ok(ApiResponse.ok(notificationResponse));
  }
}