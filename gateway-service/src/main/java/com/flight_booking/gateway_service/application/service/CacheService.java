package com.flight_booking.gateway_service.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.gateway_service.application.dto.UserInfoDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

  private static final String REDIS_KEY_PREFIX = "user:";

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  // 사용자 정보를 캐시에서 조회
  public UserInfoDto getCachedUserInfo(String token) {
    String redisKey = REDIS_KEY_PREFIX + token;
    Object cachedValue = redisTemplate.opsForValue().get(redisKey);

    if (cachedValue != null) {
      return objectMapper.convertValue(cachedValue, UserInfoDto.class);
    }
    return null;
  }

  // 사용자 정보를 캐시에 저장
  public void saveUserInfo(String token, UserInfoDto userInfo) {
    String redisKey = REDIS_KEY_PREFIX + token;
    redisTemplate.opsForValue().set(redisKey, userInfo, Duration.ofMinutes(30));
  }
}
