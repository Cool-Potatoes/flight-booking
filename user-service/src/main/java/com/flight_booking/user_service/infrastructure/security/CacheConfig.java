package com.flight_booking.user_service.infrastructure.security;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(RedisSerializer.string());  // 문자열 직렬화
    template.setValueSerializer(RedisSerializer.json()); // JSON 직렬화

    return template;
  }

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory redisConnectionFactory
  ) {
    // Redis 관련 설정 구성
    RedisCacheConfiguration configuration = RedisCacheConfiguration
        .defaultCacheConfig()
        // null 캐싱 X
        .disableCachingNullValues()
        // 기본 캐시 유지 시간 (Time To Live)
        .entryTtl(Duration.ofSeconds(60))
        // 캐시 구분 접두사 설정
        .computePrefixWith(CacheKeyPrefix.simple())
        // 캐시 저장할 값의 직렬화/역직렬화 방법
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
        );

    return RedisCacheManager
        .builder(redisConnectionFactory)
        .cacheDefaults(configuration)
        .build();
  }

}
