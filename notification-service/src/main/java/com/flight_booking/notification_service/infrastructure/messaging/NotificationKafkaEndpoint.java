package com.flight_booking.notification_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.NotificationRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.notification_service.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationKafkaEndpoint {

  private final NotificationService notificationService;

  // 비밀번호 변경을 위한 인증 번호 전송
  @KafkaListener(topics = "password-reset-topic", groupId = "notification-group")
  public void handleNotificationEvent(
      @Payload ApiResponse<NotificationRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    NotificationRequestDto requestDto = mapper.convertValue(message.getData(),
        NotificationRequestDto.class);

    notificationService.sendCode(requestDto);
  }
}