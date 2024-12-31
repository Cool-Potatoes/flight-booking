package com.flight_booking.notification_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// 알림 생성 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

  @NotNull(message = "티켓 ID는 필수입니다.")
  private Long ticketId; // 티켓 ID

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId; // 사용자 ID

  @NotNull(message = "알림 유형은 필수입니다.")
  private String notificationType; // 알림 유형

  @NotNull(message = "수신자 이메일은 필수입니다.")
  private String receiverEmail; // 수신자 이메일

  private String title;   // 알림 제목
  private String content; // 알림 내용
}
