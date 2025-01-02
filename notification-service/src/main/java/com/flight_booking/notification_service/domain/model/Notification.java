package com.flight_booking.notification_service.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification 엔티티 (p_notification 테이블에 매핑됨)
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_notification")
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "notification_id", nullable = false, unique = true)
  private UUID notificationId; // 알림 ID (PK)

  @Column(name = "ticket_id", nullable = false)
  private Long ticketId; // 티켓 ID

  @Column(name = "user_id", nullable = false)
  private Long userId; // 사용자 ID

  @Column(name = "notification_type", nullable = false, length = 50)
  private String notificationType; // 알림 유형 (예: TICKET_PURCHASE)

  @Column(name = "receiver_email", nullable = false, length = 100)
  private String receiverEmail; // 수신자 이메일

  @Column(name = "title", length = 200)
  private String title; // 알림 제목

  @Column(name = "content", columnDefinition = "TEXT")
  private String content; // 알림 내용

  @Column(name = "is_read", nullable = false)
  private boolean isRead; // 읽음 여부

  @Column(name = "is_sent", nullable = false)
  private boolean isSent; // 발송 여부

  @Column(name = "sent_at")
  private LocalDateTime sentAt; // 발송 시간

  @Column(name = "status", length = 20)
  private String status; // 상태 (SUCCESS, FAIL, PENDING)

  @Column(name = "error_message", length = 255)
  private String errorMessage; // 에러 메시지

  @Version
  @Column(name = "version", nullable = false)
  private Long version; // Optimistic Locking을 위한 버전 필드

  /**
   * 엔티티가 처음 저장되기 전에 기본값 설정
   */
  @PrePersist
  public void prePersist() {
    super.prePersist(); // BaseEntity의 prePersist 호출
    this.isRead = false;
    this.isSent = false;
    this.status = "PENDING";
  }
}
