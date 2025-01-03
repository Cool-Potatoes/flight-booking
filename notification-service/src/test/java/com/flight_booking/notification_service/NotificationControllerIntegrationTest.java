package com.flight_booking.notification_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.notification_service.application.service.NotificationService;
import com.flight_booking.notification_service.application.service.SmtpMailSender;
import com.flight_booking.notification_service.domain.model.Notification;
import com.flight_booking.notification_service.domain.repository.NotificationRepository;
import com.flight_booking.notification_service.presentation.dto.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private SmtpMailSender smtpMailSender;

  @MockBean
  private NotificationRepository notificationRepository;

  private Notification mockNotification;

  @BeforeEach
  void setUp() {
    // Mock 데이터 설정
    mockNotification = Notification.builder()
        .notificationId(UUID.randomUUID())
        .ticketId(1L)
        .userId(1L)
        .notificationType("EMAIL")
        .receiverEmail("test@example.com")
        .title("Test Title")
        .content("Test Content")
        .isRead(false)
        .isSent(false)
        .status("PENDING")
        .build();

    when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
    when(notificationRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockNotification));
  }

  @Test
  void testCreateNotification() throws Exception {
    // Mock SMTP 동작 정의
    when(smtpMailSender.send(any(String.class), any(String.class), any(String.class))).thenReturn(true);

    NotificationRequest request = new NotificationRequest(1L, 1L, "EMAIL", "test@example.com", "Test Title", "Test Content");

    mockMvc.perform(post("/v1/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.title").value("Test Title"));
  }

  @Test
  void testGetNotification() throws Exception {
    UUID id = mockNotification.getNotificationId();

    mockMvc.perform(get("/v1/notifications/{id}", id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.notificationId").value(id.toString()));
  }

  @Test
  void testGetUserNotifications() throws Exception {
    when(notificationRepository.findByUserIdAndIsDeletedFalse(any(Long.class), any()))
        .thenReturn(Mockito.mock(org.springframework.data.domain.Page.class));

    mockMvc.perform(get("/v1/notifications")
            .param("userId", "1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  void testMarkAsRead() throws Exception {
    UUID id = mockNotification.getNotificationId();

    mockMvc.perform(patch("/v1/notifications/{id}/read", id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.notificationId").value(id.toString()));
  }

  @Test
  void testDeleteNotification() throws Exception {
    UUID id = mockNotification.getNotificationId();

    mockMvc.perform(delete("/v1/notifications/{id}", id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
