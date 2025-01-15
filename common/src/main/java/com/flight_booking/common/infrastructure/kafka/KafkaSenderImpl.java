package com.flight_booking.common.infrastructure.kafka;

import com.flight_booking.common.presentation.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSenderImpl implements KafkaSender {

  private final KafkaTemplate<String, ApiResponse<?>> apiKafkaTemplate;

  @Override
  public void sendApiResponseDataWithFrom(String topic, String key, Object data, String methodName,
      String className) {
    apiKafkaTemplate.send(
        topic,
        key,
        ApiResponse.ok(data, "from " + methodName + " [" + className + "]")
    );
  }
}
