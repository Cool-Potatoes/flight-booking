package com.flight_booking.flight_service.application.service;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

  private final AiService aiService;
  private final StringRedisTemplate redisTemplate;

  private static final String REGULATION_CACHE_PREFIX = "regulation:";

  // 기내 수하물 규정
  public ApiResponse<?> saveRegulationAnswer(String airline, String seatClass) {
    return saveAnswer(REGULATION_CACHE_PREFIX + airline + ":" + seatClass,
        () -> aiService.getRegulation(airline, seatClass));
  }

  // 데이터 저장 및 조회
  private ApiResponse<?> saveAnswer(String redisKey, ApiResponseProvider apiResponseProvider) {
    ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

    // 데이터 조회
    String cachedAnswer = valueOps.get(redisKey);
    if (cachedAnswer != null) {
      return ApiResponse.ok(cachedAnswer, "Redis에서 데이터를 반환했습니다.");
    }

    try {
      // 캐시에 데이터가 없을 경우 API 호출
      String apiResponse = apiResponseProvider.getApiResponse();
      String answerText = parseApiResponse(apiResponse);

      // Redis에 데이터 저장 (1일)
      valueOps.set(redisKey, answerText, 1, TimeUnit.DAYS);

      return ApiResponse.ok(answerText, "새로운 데이터를 생성하여 저장했습니다.");
    } catch (Exception e) {
      throw new RuntimeException("Answer 저장 중 오류 발생", e);
    }
  }

  // API 응답 파싱
  private String parseApiResponse(String apiResponse) {
    return apiResponse.trim();
  }

  // API 응답 제공자 인터페이스
  @FunctionalInterface
  private interface ApiResponseProvider {

    String getApiResponse() throws Exception;
  }
}