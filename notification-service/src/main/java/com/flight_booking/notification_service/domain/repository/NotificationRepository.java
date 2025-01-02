package com.flight_booking.notification_service.domain.repository;

import com.flight_booking.notification_service.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 사용자 ID로 알림 조회 (페이징 지원)
  Page<Notification> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

  // 알림 소프트 삭제
  @Transactional
  @Modifying
  @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = CURRENT_TIMESTAMP WHERE n.notificationId = :notificationId")
  void softDelete(UUID notificationId);
}
