package com.flight_booking.notification_service.model;

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
  private UUID notificationId; // 알림 ID (PK)

  @Column(nullable = false)
  private Long ticketId; // 티켓 ID

  @Column(nullable = false)
  private Long userId; // 사용자 ID

  @Column(nullable = false)
  private String notificationType; // 알림 유형 (예: TICKET_PURCHASE)

  @Column(nullable = false)
  private String receiverEmail; // 수신자 이메일

  private String title; // 알림 제목
  private String content; // 알림 내용

  @Column(nullable = false)
  private boolean isRead; // 읽음 여부

  private boolean isSent; // 발송 여부
  private LocalDateTime sentAt; // 발송 시간

  private String status; // 상태 (SUCCESS, FAIL, PENDING)
  private String errorMessage; // 에러 메시지

  @Version
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
