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

  @Value("${spring.gemini.api.url}")
  private String GEMINI_API_URL;
  @Value("${spring.gemini.api.key}")
  private String GEMINI_API_KEY;

  private final RestClient restClient;

  // 기내 수하물 규정 조회
  public String getRegulation(String airline, String seatClass) {
    String userQuestion = regulationQuestion(airline, seatClass);
    return callGeminiApi(userQuestion);
  }

  // 기내 수하물 규정 질문
  private String regulationQuestion(String airline, String seatClass) {
    return airline + "의 홈페이지를 참고하여 " + seatClass + " 좌석의 기내 수하물 규정을 간결하고 공손하게 50자 이하로 알려줘."
        + " 아래 예시를 참고해줘."
        + "항공사, 좌석 등급의 기내 수하물은 휴대용 가방 1개와 개인 소지품 1개이며, 총 무게는 10kg 이하입니다. "
        + "휴대용 가방의 크기는 합 115cm 이하여야 합니다."
        + "자세한 내용은 항공사 공식 홈페이지를 확인해 주세요. (단, 규정은 변동될 수 있습니다.)";
  }

  // 여행지의 평균 날씨 조회
  public String getWeather(String country, String month) {
    String userQuestion = weatherQuestion(country, month);
    return callGeminiApi(userQuestion);
  }

  // 여행지의 평균 날씨 질문
  private String weatherQuestion(String country, String month) {
    return "과거 날씨 데이터를 바탕으로 " + country + "의 " + month
        + " 평균 날씨와 여행하기 좋은 조건, 유의사항을 50자 이하로 간결하게 알려줘. "
        + "예: 날씨가 여행하기 좋은지, 우기나 폭염 등 특별한 날씨 상황이 있는지 포함해줘.";
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