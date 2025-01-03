package com.flight_booking.notification_service.presentation.controller;

import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import com.flight_booking.notification_service.presentation.dto.NotificationResponse;
import com.flight_booking.notification_service.application.service.NotificationService;
import com.flight_booking.notification_service.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@RequestBody NotificationRequest request) {
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
    Page<NotificationResponse> response = notificationService.getNotificationsByUserId(userId, pageable);
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
}