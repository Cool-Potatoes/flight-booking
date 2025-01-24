package com.flight_booking.flight_service.presentation.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.AnswerService;
import com.flight_booking.flight_service.presentation.request.RegulationQuestionDto;
import com.flight_booking.flight_service.presentation.request.WeatherQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/flights/ai")
public class AiController {

  private final AnswerService answerService;

  @PostMapping("/regulation")
  public ApiResponse<?> getRegulation(@RequestBody RegulationQuestionDto questionDto) {
    String airline = questionDto.airline().replace(" ", "");
    String seatClass = questionDto.seatClass();
    return answerService.saveRegulationAnswer(airline, seatClass);
  }

  // TODO 날씨 API 접목 필요
  @PostMapping("/weather")
  public ApiResponse<?> getWeather(@RequestBody WeatherQuestionDto questionDto) {
    String country = questionDto.country();
    String month = questionDto.month();
    return answerService.saveWeatherAnswer(country, month);
  }
}