package com.flight_booking.common.infrastructure.kafka;

public interface KafkaSender {

  void sendApiResponseDataWithMessage(String topic, String key, Object data, String from);

}
