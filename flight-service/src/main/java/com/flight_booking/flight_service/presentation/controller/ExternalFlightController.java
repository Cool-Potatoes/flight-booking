package com.flight_booking.flight_service.presentation.controller;


import com.flight_booking.flight_service.application.service.FlightService;
import com.flight_booking.flight_service.presentation.global.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flights")
@RequiredArgsConstructor
public class ExternalFlightController {
  private final FlightService flightService;

  @GetMapping("/{flightId}")
  public ApiResponse<?> getFlightById(@PathVariable UUID flightId){
    return ApiResponse.ok(null);
  }


}
