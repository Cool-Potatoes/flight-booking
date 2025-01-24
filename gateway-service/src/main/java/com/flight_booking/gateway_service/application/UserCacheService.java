package com.flight_booking.gateway_service.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.gateway_service.infrastructure.JwtUtil;
import com.flight_booking.gateway_service.infrastructure.feign.UserFeignClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

  private static final String REDIS_KEY_PREFIX = "user:";

  private final UserFeignClient userFeignClient;
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;

  // 주어진 토큰을 통해 사용자 정보를 캐시에서 조회, 없으면 서비스에서 조회하여 캐시에 저장
  public UserInfo getUserInfo(String token) {

    String email = jwtUtil.extractEmail(token);
    String role = jwtUtil.extractRole(token);

    // ADMIN: 캐싱X, 매번 조회
    if ("ADMIN".equals(role)) {
      return getUserInfoFromService(email, role);
    }

    String redisKey = REDIS_KEY_PREFIX + token;

    Object cachedValue = redisTemplate.opsForValue().get(redisKey);
    if (cachedValue != null) {
      return objectMapper.convertValue(cachedValue, UserInfo.class);
    }

    UserInfo userInfo = getUserInfoFromService(email, role);
    redisTemplate.opsForValue().set(redisKey, userInfo, Duration.ofMinutes(30));
    return userInfo;
  }

  // 사용자 정보를 서비스에서 조회하여 반환
  private UserInfo getUserInfoFromService(String email, String role) {
    UserStatusDto userStatus = userFeignClient.getUserStatus(email);

    return UserInfo.builder()
        .email(email)
        .role(role)
        .isBlocked(userStatus.isBlocked())
        .isDeleted(userStatus.isDeleted())
        .build();
  }
}
