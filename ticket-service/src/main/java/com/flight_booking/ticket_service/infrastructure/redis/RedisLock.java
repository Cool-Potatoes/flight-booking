package com.flight_booking.ticket_service.infrastructure.redis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLock {

  private final String LOCK_KEY_PREFIX = "seat-lock:"; // Key Prefix
  private final RedisTemplate<String, String> redisTemplate;

  /**
   * Lock 획득 시도
   *
   * @param seatId   UUID 형식의 좌석 ID
   * @param timeout  락 유지 시간
   * @param timeUnit 시간 단위
   * @return 락 획득 성공 여부
   */
  public boolean tryLock(UUID seatId, long timeout, TimeUnit timeUnit) {
    String lockKey = LOCK_KEY_PREFIX + seatId.toString();
    Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", timeout, timeUnit);
    return Boolean.TRUE.equals(success);
  }

  /**
   * Lock 해제
   *
   * @param seatId UUID 형식의 좌석 ID
   */
  public void unlock(UUID seatId) {
    String lockKey = LOCK_KEY_PREFIX + seatId.toString();
    redisTemplate.delete(lockKey);
  }
}