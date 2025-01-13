package com.flight_booking.booking_service.infrastructure.messaging;

import com.flight_booking.common.infrastructure.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingKafkaSender {

  private final KafkaSender kafkaSender;

  public void sendMessage(String topic, String key, Object data, String from) {
    kafkaSender.sendApiResponseDataWithMessage(topic, key, data, from);
  }

}
