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

  private static final String REGULATION_CACHE_PREFIX = "ai:regulation:";
  private static final String WEATHER_CACHE_PREFIX = "ai:weather:";

  private final AiService aiService;
  private final StringRedisTemplate redisTemplate;

  // 기내 수하물 규정 응답 Redis에 저장
  public ApiResponse<?> saveRegulationAnswer(String airline, String seatClass) {
    return saveAnswer(REGULATION_CACHE_PREFIX + airline + ":" + seatClass,
        () -> aiService.getRegulation(airline, seatClass));
  }

  // 여행지의 평균 날씨 응답 Redis에 저장
  public ApiResponse<?> saveWeatherAnswer(String country, String month) {
    return saveAnswer(WEATHER_CACHE_PREFIX + country + ":" + month,
        () -> aiService.getWeather(country, month));
  }

  // 데이터 저장 및 조회
  private ApiResponse<?> saveAnswer(String redisKey, ApiResponseProvider apiResponseProvider) {
    ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

    String cachedAnswer = valueOps.get(redisKey);
    if (cachedAnswer != null) {
      return ApiResponse.ok(cachedAnswer, "Redis에서 데이터를 반환했습니다.");
    }

    try {
      String apiResponse = apiResponseProvider.getApiResponse();
      String answerText = parseApiResponse(apiResponse);

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