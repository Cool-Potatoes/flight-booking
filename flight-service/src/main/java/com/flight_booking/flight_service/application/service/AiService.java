package com.flight_booking.flight_service.application.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AiService {

  @Value("${gemini.api.url}")
  private String GEMINI_API_URL;
  @Value("${gemini.api.key}")
  private String GEMINI_API_KEY;

  private final RestClient restClient;

  // 기내 수하물 규정 조회
  public String getRegulation(String airline, String seatClass) {
    String userQuestion = regulationQuestion(airline, seatClass);
    return callGeminiApi(userQuestion);
  }

  // 기내 수하물 규정 질문
  private String regulationQuestion(String airline, String seatClass) {
    return airline + "의 " + seatClass + "좌석의 기내 수하물 규정(크기, 무게, 제한 물품 등)을 간결하고 공손하게 40자 이하로 알려주세요.";
  }

  // 여행지의 평균 날씨 조회
  public String getWeather(String country, String month) {
    String userQuestion = weatherQuestion(country, month);
    return callGeminiApi(userQuestion);
  }

  // 여행지의 평균 날씨 질문
  private String weatherQuestion(String country, String Month) {
    return "과거 날씨 데이터를 기반으로 " + country + "의 " + Month
        + " 평균 기후와 여행 시 유의사항을 간결하고 공손하게 40자 이하로 설명해주세요.";
  }

  // Gemini API 호출 및 응답 처리
  private String callGeminiApi(String userQuestion) {
    JSONObject jsonRequest = new JSONObject();
    jsonRequest.put("contents", new JSONArray()
        .put(new JSONObject()
            .put("parts", new JSONArray()
                .put(new JSONObject()
                    .put("text", userQuestion)))));

    try {
      ResponseEntity<String> response = restClient
          .post()
          .uri(GEMINI_API_URL + "?key=" + GEMINI_API_KEY)
          .body(jsonRequest.toString())
          .retrieve()
          .toEntity(String.class);

      String responseBody = response.getBody();

      JSONObject jsonResponse = new JSONObject(responseBody);
      return jsonResponse
          .getJSONArray("candidates")
          .getJSONObject(0)
          .getJSONObject("content")
          .getJSONArray("parts")
          .getJSONObject(0)
          .getString("text");
    } catch (
        JSONException e) {
      return "API 응답에서 예상한 데이터가 없습니다.";
    }
  }
}