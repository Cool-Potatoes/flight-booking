package com.flight_booking.flight_service.presentation.controller;


import com.flight_booking.flight_service.application.service.FlightService;
import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.presentation.global.ApiResponse;
import com.flight_booking.flight_service.presentation.request.FlightRequestDto;
import com.flight_booking.flight_service.presentation.response.FlightResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flights")
@RequiredArgsConstructor
public class FlightController {

  private final FlightService flightService;

  @GetMapping("/{flightId}")
  public ApiResponse<?> getFlightById(@PathVariable UUID flightId) {
    return ApiResponse.ok(flightService.getFlightById(flightId), "항공편 조회 성공");
  }

  @GetMapping
  public ApiResponse<?> getFlightsPage(
      @RequestParam(required = false) List<UUID> uuidList,
      @QuerydslPredicate(root = Flight.class) Predicate predicate,
      Pageable pageable
  ) {

    PagedModel<FlightResponseDto> flightResponseDtoPagedModel
        = flightService.getFlightsPage(uuidList, predicate, pageable);

    return ApiResponse.ok(flightResponseDtoPagedModel, "항공편 데이터 목록 조회 성공");
  }

  @PostMapping
  public ApiResponse<?> createFlight(@RequestBody FlightRequestDto requestDto) {
    return ApiResponse.ok(flightService.createFlight(requestDto), "항공편 생성 성공");
  }

  @PatchMapping("/{flightId}")
  public ApiResponse<?> updateFlight(@PathVariable UUID flightId,
      @RequestBody FlightRequestDto requestDto) {
    return ApiResponse.ok(flightService.updateFlight(flightId, requestDto), "항공편 수정 성공");
  }

  @DeleteMapping("/{flightId}")
  public ApiResponse<?> deleteFlight(@PathVariable UUID flightId) {
    return ApiResponse.ok(flightService.deleteFlight(flightId), "항공편 삭제 성공");
  }

}
