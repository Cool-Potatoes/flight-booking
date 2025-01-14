package com.flight_booking.common.infrastructure.kafka;

public interface KafkaSender {

  void sendApiResponseDataWithFrom(String topic, String key, Object data, String methodName, String className);

}
