package com.flight_booking.common.infrastructure.kafka;

public interface KafkaSender {

  void sendApiResponseData(String topic, String key, Object data);

}
