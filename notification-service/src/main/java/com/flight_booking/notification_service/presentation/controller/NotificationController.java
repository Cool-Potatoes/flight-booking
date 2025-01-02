package com.flight_booking.notification_service.presentation.controller;

import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import com.flight_booking.notification_service.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  // [1] 알림 생성 (수동 API, 혹은 다른 서비스 호출 시)
  @PostMapping
  public ResponseEntity<NotificationResponse> createNotification(
      @RequestBody NotificationRequest request
  ) {
    return ResponseEntity.ok(notificationService.createNotification(request));
  }

  // [2] 특정 알림 조회
  @GetMapping("/{id}")
  public ResponseEntity<NotificationResponse> getNotification(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.getNotificationById(id));
  }

  // [3] 사용자 알림 목록
  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getUserNotifications(
      @RequestParam Long userId
  ) {
    return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
  }

  // [4] 알림 읽음 처리
  @PatchMapping("/{id}/read")
  public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.markAsRead(id));
  }

  // [5] 알림 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteNotification(@PathVariable UUID id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.ok("알림이 삭제되었습니다.");
  }
}
