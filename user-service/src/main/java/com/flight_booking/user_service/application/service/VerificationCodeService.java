package com.flight_booking.user_service.application.service;

import com.flight_booking.common.application.dto.NotificationRequestDto;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.jwt.JwtUtil;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.SendCodeRequest;
import com.flight_booking.user_service.presentation.request.VerificationCodeRequest;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

  private final UserRepository userRepository;
  private final RedisCacheManager redisCacheManager;
  private final KafkaTemplate<String, NotificationRequestDto> kafkaTemplate;
  private final JwtUtil jwtUtil;

  // 비밀번호 찾기: 인증번호 발급 (비밀번호 찾기)
  @Transactional
  public void sendVerificationCode(SendCodeRequest request) {
    // 이메일 확인
    User user = getUser(request.email());

    // 임시 재발급 코드 생성
    String email = user.getEmail();
    String code = createCode();
    Long userId = user.getId();

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
      throw new IllegalArgumentException("캐시 초기화 오류" + cacheName);
    }

    // 캐시가 정상적으로 존재하면, 인증 코드 저장
    cache.put(email, code);

    // 이메일 전송 위한 카프카 이벤트 발행
    kafkaTemplate.send(
        "password-reset-topic", email,
        new NotificationRequestDto(userId, email, code));

    log.info("인증 코드 전송 완료: 이메일 = {}, 코드 = {}", email, code);
  }

  // 비밀번호 찾기: 인증번호 검증
  public String verifyCode(VerificationCodeRequest request) {

    // 이메일로 캐시에서 인증 코드 가져오기
    String email = request.email();
    String storedCode = redisCacheManager.getCache("verification_code_cache")
        .get(email, String.class);  // 캐시에서 이메일로 저장된 인증 코드 가져오기

    // 코드 존재 여부 확인
    if (storedCode == null) {
      throw new UserException(ErrorCode.CODE_NOT_FOUND);
    }

    // 사용자가 입력한 코드와 비교
    if (!storedCode.equals(request.code())) {
      throw new UserException(ErrorCode.INVALID_CODE);
    }

    // 인증 코드가 유효한 경우 임시 토큰 생성
    User user = getUser(email);
    String role = user.getRole().getAuthority();

    return jwtUtil.createToken(email, role);
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