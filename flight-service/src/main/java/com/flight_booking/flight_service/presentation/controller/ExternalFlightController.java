package com.flight_booking.flight_service.presentation.controller;


import com.flight_booking.flight_service.application.service.FlightService;
import com.flight_booking.flight_service.presentation.global.ApiResponse;
import com.flight_booking.flight_service.presentation.request.FlightCreateRequestDto;
import com.flight_booking.flight_service.presentation.request.FlightUpdateRequestDto;
import com.flight_booking.flight_service.presentation.response.FlightDetailResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flights")
@RequiredArgsConstructor
public class ExternalFlightController {

  private final FlightService flightService;

  @GetMapping("/{flightId}")
  public ApiResponse<?> getFlightById(@PathVariable UUID flightId) {
    return ApiResponse.ok(flightService.getFlightById(flightId), "항공편 조회 성공");
  }

  //TODO: 검색, 정렬 추후 구현
  @GetMapping("")
  public ApiResponse<?> getFlights() {
    return null;
  }

  @PostMapping("")
  public ApiResponse<?> createFlight(@RequestBody FlightCreateRequestDto requestDto) {
    return ApiResponse.ok(flightService.createFlight(requestDto), "항공편 생성 성공");
  }

  @PatchMapping("/{flightId}")
  public ApiResponse<?> updateFlight(@PathVariable UUID flightId,
      @RequestBody FlightUpdateRequestDto requestDto) {
    return ApiResponse.ok(flightService.updateFlight(flightId, requestDto), "항공편 수정 성공");
  }

  @DeleteMapping("/{flightId}")
  public ApiResponse<?> deleteFlight(@PathVariable UUID flightId){
    return ApiResponse.ok(flightService.deleteFlight(flightId), "항공편 삭제 성공");
  }

}
