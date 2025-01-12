package com.flight_booking.notification_service.global.configuration;

import com.flight_booking.common.application.dto.NotificationRequestDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

// 이 클래스는 Kafka 컨슈머 설정을 위한 Spring 설정 클래스입니다.
@EnableKafka // Kafka 리스너를 활성화하는 어노테이션입니다.
@Configuration // Spring 설정 클래스로 선언하는 어노테이션입니다.
public class ConsumerApplicationKafkaConfig {

  // Kafka 컨슈머 팩토리를 생성하는 빈을 정의합니다.
  // ConsumerFactory는 Kafka 컨슈머 인스턴스를 생성하는 데 사용됩니다.
  // 각 컨슈머는 이 팩토리를 통해 생성된 설정을 기반으로 작동합니다.
  @Bean
  public ConsumerFactory<String, NotificationRequestDto> consumerFactory() {
    // 컨슈머 팩토리 설정을 위한 맵을 생성
    Map<String, Object> configProps = new HashMap<>();
    // Kafka 브로커의 주소를 설정
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // 메시지 키의 디시리얼라이저 클래스를 설정
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    // 디시리얼라이제이션 오류가 발생해도 컨슈머가 중단되지 않도록 설정
    configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    // 메세지 값의 디시리어라이저 클래스 설정
    JsonDeserializer<NotificationRequestDto> jsonDeserializer = new JsonDeserializer<>(
        NotificationRequestDto.class);
    // 특정 클래스(메세지 값)만 신뢰하도록 설정
    jsonDeserializer.addTrustedPackages(NotificationRequestDto.class.getPackage().getName());

    return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
        jsonDeserializer);
  }

  // Kafka 리스너 컨테이너 팩토리를 생성하는 빈을 정의합니다.
  // ConcurrentKafkaListenerContainerFactory는 Kafka 메시지를 비동기적으로 수신하는 리스너 컨테이너를 생성하는 데 사용됩니다.
  // 이 팩토리는 @KafkaListener 어노테이션이 붙은 메서드들을 실행할 컨테이너를 제공합니다.
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, NotificationRequestDto> kafkaListenerContainerFactory() {
    // ConcurrentKafkaListenerContainerFactory를 생성합니다.
    ConcurrentKafkaListenerContainerFactory<String, NotificationRequestDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
    // 컨슈머 팩토리를 리스너 컨테이너 팩토리에 설정합니다.
    factory.setConsumerFactory(consumerFactory());
    // 설정된 리스너 컨테이너 팩토리를 반환합니다.
    return factory;
  }
}