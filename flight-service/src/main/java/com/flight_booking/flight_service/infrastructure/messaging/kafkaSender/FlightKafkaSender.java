package com.flight_booking.flight_service.infrastructure.messaging.kafkaSender;

import com.flight_booking.common.infrastructure.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightKafkaSender {

  private final KafkaSender kafkaSender;

  public void sendMessage(String topic, String key, Object data) {
    kafkaSender.sendApiResponseData(topic, key, data);
  }

}
