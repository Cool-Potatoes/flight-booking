package com.flight_booking.notification_service;

import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;

  private Notification notification1;

  @BeforeEach
  public void setUp() {
    // 테스트용 데이터 초기화 및 생성
    notificationRepository.deleteAll();
    notification1 = notificationRepository.save(
        Notification.builder()
            .userId(1L)
            .ticketId(12345L)
            .notificationType("TICKET_PURCHASE")
            .receiverEmail("test@example.com")
            .isRead(false)
            .status("PENDING")
            .build()
    );
  }

  @Test
  public void testMarkAsRead_Success() throws Exception {
    // 알림 읽음 처리 API 테스트
    mockMvc.perform(
            patch("/v1/notifications/{id}/read", notification1.getNotificationId())
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
        .andExpect(jsonPath("$.notificationId").value(notification1.getNotificationId().toString())) // 알림 ID 확인
        .andExpect(jsonPath("$.isRead").value(true)); // 읽음 상태 확인
  }

  @Test
  public void testGetUserNotificationsWithPaging() throws Exception {
    // 페이징된 알림 목록 조회 테스트
    mockMvc.perform(
            get("/v1/notifications")
                .param("userId", "1") // 사용자 ID 전달
                .param("page", "0")   // 첫 번째 페이지 요청
                .param("size", "10")  // 페이지 크기 요청
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
        .andExpect(jsonPath("$.content").isArray()) // 페이징된 데이터가 배열인지 확인
        .andExpect(jsonPath("$.content[0].userId").value(1L)); // 첫 번째 알림의 사용자 ID 확인
  }
}

