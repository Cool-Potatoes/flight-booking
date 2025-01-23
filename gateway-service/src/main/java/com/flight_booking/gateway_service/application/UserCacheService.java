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

  public UserInfo getUserInfo(String token) {

    // 토큰에서 이메일, 역할 추출
    String email = jwtUtil.extractEmail(token);
    String role = jwtUtil.extractRole(token);

    // ADMIN: 캐싱X, 매번 조회
    if ("ADMIN".equals(role)) {
      return getUserInfoFromService(email, role);
    }

    String redisKey = REDIS_KEY_PREFIX + token;

    // 캐시에서 정보 조회
    Object cachedValue = redisTemplate.opsForValue().get(redisKey);
    if (cachedValue != null) {
      return objectMapper.convertValue(cachedValue, UserInfo.class);
    }

    // 캐시가 없으면 서비스에서 조회
    UserInfo userInfo = getUserInfoFromService(email, role);
    redisTemplate.opsForValue().set(redisKey, userInfo, Duration.ofMinutes(30));
    return userInfo;
  }

  // 사용자 상태 조회
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
