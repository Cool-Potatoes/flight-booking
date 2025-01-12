package com.flight_booking.user_service.infrastructure.messaging;

import com.flight_booking.common.infrastructure.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKafkaSender {

  private final KafkaSender kafkaSender;

  public void sendMessage(String topic, String key, Object data) {
    kafkaSender.sendApiResponseData(topic, key, data);
  }

}
