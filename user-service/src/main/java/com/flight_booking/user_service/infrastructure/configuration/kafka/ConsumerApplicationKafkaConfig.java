package com.flight_booking.user_service.infrastructure.configuration.kafka;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class ConsumerApplicationKafkaConfig {

  @Value("${service.kafka.url}")
  private String url;

  // Kafka 컨슈머 팩토리 생성 빈 정의
  @Bean
  public ConsumerFactory<String, ApiResponse<?>> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    JsonDeserializer<ApiResponse<?>> jsonDeserializer = new JsonDeserializer<>(ApiResponse.class);
    jsonDeserializer.addTrustedPackages(ApiResponse.class.getPackage().getName());
    return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
        jsonDeserializer);
  }

  // Kafka 리스너 컨테이너 팩토리 생성 빈 정의
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ApiResponse<?>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ApiResponse<?>> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}