package com.flight_booking.gateway_service.application.service;

import com.flight_booking.gateway_service.application.dto.UserInfoDto;
import com.flight_booking.gateway_service.application.dto.UserStatusDto;
import com.flight_booking.gateway_service.infrastructure.JwtUtil;
import com.flight_booking.gateway_service.infrastructure.feign.UserFeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

  private final UserFeignClient userFeignClient;
  private final CacheService cacheService;
  private final JwtUtil jwtUtil;

  public UserInfoService(@Lazy UserFeignClient userFeignClient, CacheService cacheService,
      JwtUtil jwtUtil) {
    this.userFeignClient = userFeignClient;
    this.cacheService = cacheService;
    this.jwtUtil = jwtUtil;
  }

  public UserInfoDto getUserInfo(String token) {
    String email = jwtUtil.extractEmail(token);
    String role = jwtUtil.extractRole(token);

    // ADMIN: 캐싱X, 매번 조회
    if ("ADMIN".equals(role)) {
      return getUserInfoFromService(email, role);
    }

    UserInfoDto userInfo = cacheService.getCachedUserInfo(token);
    if (userInfo != null) {
      return userInfo;
    }

    userInfo = getUserInfoFromService(email, role);
    cacheService.saveUserInfo(token, userInfo);
    return userInfo;
  }

  // 사용자 정보 조회
  private UserInfoDto getUserInfoFromService(String email, String role) {
    UserStatusDto userStatusDto = userFeignClient.getUserStatus(email);

    return UserInfoDto.builder()
        .email(email)
        .role(role)
        .isBlocked(userStatusDto.isBlocked())
        .isDeleted(userStatusDto.isDeleted())
        .build();
  }
}
