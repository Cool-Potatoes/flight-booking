package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.SendCodeRequest;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

  private final UserRepository userRepository;
  private final RedisCacheManager redisCacheManager;

  // 비밀번호 찾기: 인증번호 발급
  @Transactional
  public void sendVerificationCode(SendCodeRequest request) {
    // 이메일 확인
    User user = getUser(request.email());

    // 임시 재발급 코드 생성
    String email = user.getEmail();
    String code = createCode();

    String cacheName = "verification_code_cache";

    // 캐시에서 인증 코드 조회
    String storedCode = redisCacheManager.getCache(cacheName).get(email, String.class);

    // 인증 코드가 이미 존재하면 예외 처리
    if (storedCode != null) {
      throw new UserException(ErrorCode.CODE_ALREADY_SENT);
    }

    // 캐시에 인증 코드 저장
    var cache = redisCacheManager.getCache(cacheName);
    if (cache == null) {
      throw new IllegalArgumentException("캐시 초기화 오류");
    }

    // 캐시가 정상적으로 존재하면, 인증 코드 저장
    cache.put(email, code);

    log.info("인증 코드 전송 완료: 이메일 = {}, 코드 = {}", email, code);
  }

  // 인증 코드 생성
  public String createCode() {
    Random random = new Random();
    StringBuilder code = new StringBuilder();

    for (int i = 0; i < 6; i++) {
      int numbers = random.nextInt(10);
      code.append(numbers);
    }

    return code.toString();
  }

  // 이메일로 사용자 찾기
  private User getUser(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }
}