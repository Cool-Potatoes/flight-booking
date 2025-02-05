package com.flight_booking.notification_service.domain.model;

import com.flight_booking.common.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_notification")
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "notification_id", nullable = false, unique = true)
  private UUID notificationId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "notification_type", nullable = false, length = 50)
  private String notificationType;

  @Column(name = "receiver_email", nullable = false, length = 100)
  private String receiverEmail;

  @Column(name = "title", length = 200)
  private String title;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "is_read", nullable = false)
  private boolean isRead;

  @Column(name = "is_sent", nullable = false)
  private boolean isSent;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "status", length = 20)
  private String status;

  @Column(name = "error_message", length = 255)
  private String errorMessage;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  // 알림 상태 업데이트 메서드 (수정됨)
  public void updateStatus(boolean emailSent) {
    this.isSent = emailSent;
    this.sentAt = emailSent ? LocalDateTime.now() : null;
    this.status = emailSent ? "SUCCESS" : "FAIL";
    this.errorMessage = emailSent ? null : "Failed to send email";
  }
  public void setRead(boolean isRead) {
    this.isRead = isRead;
  }
}
