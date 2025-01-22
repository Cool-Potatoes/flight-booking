package com.flight_booking.gateway_service.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.gateway_service.infrastructure.feign.UserFeignClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFeignService {

  private static final String REDIS_KEY_PREFIX = "userStatus:";

  private final UserFeignClient userFeignClient;
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public UserStatusDto getUserStatus(String email) {
    String redisKey = REDIS_KEY_PREFIX + email;

    Object cachedValue = redisTemplate.opsForValue().get(redisKey);

    if (cachedValue != null) {
      return objectMapper.convertValue(cachedValue, UserStatusDto.class);
    }

    UserStatusDto userStatus = userFeignClient.getUserStatus(email);
    redisTemplate.opsForValue().set(redisKey, userStatus, Duration.ofMinutes(30));
    return userStatus;
  }
}
