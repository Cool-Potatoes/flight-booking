package com.flight_booking.notification_service;

import com.flight_booking.notification_service.model.Notification;
import com.flight_booking.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Security 필터를 비활성화하여 테스트 단순화
class NotificationControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;

  private Notification notification1;

  @BeforeEach
  public void setUp() {
    // [1] 테스트 전 데이터 초기화
    notificationRepository.deleteAll();

    // [2] 테스트용 알림 데이터 추가
    notification1 = notificationRepository.save(
        Notification.builder()
            .userId(1L) // 테스트 사용자 ID
            .ticketId(12345L) // 필수 필드인 ticketId 설정 (Long 타입)
            .notificationType("TICKET_PURCHASE") // 알림 타입
            .receiverEmail("test@example.com") // 수신자 이메일
            .isRead(false) // 초기 읽음 상태: false
            .status("PENDING") // 초기 상태
            .build()
    );
  }

  @Test
  public void testMarkAsRead_Success() throws Exception {
    // [3] 읽음 처리 API 호출 테스트
    mockMvc.perform(
            patch("/v1/notifications/{id}/read", notification1.getNotificationId()) // 경로 매핑
                .contentType(MediaType.APPLICATION_JSON) // Content-Type 설정
        )
        .andExpect(status().isOk()) // HTTP 상태 코드 200 기대
        .andExpect(jsonPath("$.notificationId").value(notification1.getNotificationId().toString())) // 반환된 알림 ID 검증
        .andExpect(jsonPath("$.isRead").value(true)); // 읽음 상태가 true로 업데이트되었는지 검증
  }
}
