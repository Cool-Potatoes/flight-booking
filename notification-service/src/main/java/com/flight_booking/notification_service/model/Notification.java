package com.flight_booking.notification_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 알림 엔티티 (p_notification 테이블)
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
  private UUID notificationId;  // 알림 ID

  @Column(nullable = false)
  private Long ticketId;   // 티켓 ID

  @Column(nullable = false)
  private Long userId;     // 사용자 ID

  @Column(nullable = false)
  private String notificationType; // 알림 유형 (예: TICKET_PURCHASE)

  @Column(nullable = false)
  private String receiverEmail;    // 수신자 이메일

  private String title;   // 알림 제목
  private String content; // 알림 내용

  @Column(nullable = false)
  private boolean isRead; // 읽음 여부

  private boolean isSent; // 발송 여부 (이메일 전송 성공 여부)
  private LocalDateTime sentAt; // 발송 일시

  private String status;      // 상태 (SUCCESS, FAIL, PENDING)
  private String errorMessage; // 에러 메시지

  @PrePersist
  public void prePersist() {
    super.prePersist(); // BaseEntity의 prePersist 호출
    this.isRead = false;
    this.isSent = false;
    this.status = "PENDING";
  }
}
