package com.flight_booking.user_service.infrastructure.configuration.kafka;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class ProducerApplicationKafkaConfig {

  @Value("${service.kafka.url}")
  private String url;

  // Kafka 프로듀서 관련 Bean 설정
  @Bean
  public ProducerFactory<String, ApiResponse<?>> apiResponseProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  // KafkaTemplate 생성
  @Bean
  public KafkaTemplate<String, ApiResponse<?>> apiResponseKafkaTemplate() {
    return new KafkaTemplate<>(apiResponseProducerFactory());
  }
}