package com.flight_booking.user_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.UserRefundRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserKafkaEndpoint {

  private final UserService userService;

  @KafkaListener(groupId = "user-mileage-group", topics = "user-update-mileage-topic")
  public void consumeUpdateMileage(@Payload ApiResponse<UserRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    UserRequestDto userRequestDto = mapper.convertValue(message.getData(),
        UserRequestDto.class);

    userService.updateUserMileage(userRequestDto);
  }

  @KafkaListener(groupId = "user-refund-group", topics = "user-refund-topic")
  public void consumeUserRefund(
      @Payload ApiResponse<UserRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    UserRefundRequestDto userRefundRequestDto = mapper.convertValue(message.getData(),
        UserRefundRequestDto.class);

    userService.refundPayment(userRefundRequestDto);
  }
}
