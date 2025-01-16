package com.flight_booking.flight_service.presentation.internal.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.FlightService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/flights")
@RequiredArgsConstructor
public class InternalFlightController {

  private final FlightService flightService;

  @GetMapping("/{seatId}")
  public ApiResponse<?> checkFlightBySeatId(@PathVariable UUID seatId) {
    return ApiResponse.ok(flightService.checkFlightStatusBySeatId(seatId), "항공편 체크 성공");
  }
}
