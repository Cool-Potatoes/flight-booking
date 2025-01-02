package com.flight_booking.notification_service.presentation.controller;

import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import com.flight_booking.notification_service.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  // 알림 생성
  @PostMapping
  public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
    return ResponseEntity.ok(notificationService.createNotification(request));
  }

  // 특정 알림 조회
  @GetMapping("/{id}")
  public ResponseEntity<NotificationResponse> getNotification(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.getNotificationById(id));
  }

  // 사용자 알림 목록 조회 (페이징)
  @GetMapping
  public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
      @RequestParam Long userId, Pageable pageable) {
    return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId, pageable));
  }

  // 알림 읽음 처리
  @PatchMapping("/{id}/read")
  public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.markAsRead(id));
  }

  // 알림 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteNotification(@PathVariable UUID id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.ok("Notification deleted successfully.");
  }
}
