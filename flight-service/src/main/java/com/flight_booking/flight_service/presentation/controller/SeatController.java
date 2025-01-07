package com.flight_booking.flight_service.presentation.controller;

import com.flight_booking.flight_service.application.service.SeatService;
import com.flight_booking.flight_service.presentation.global.ApiResponse;
import com.flight_booking.flight_service.presentation.request.SeatRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flights/{flightId}/seats")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;

  @PatchMapping("/price")
  public ApiResponse<?> updateFlightWholeSeatPrice(
      @PathVariable UUID flightId,
      @RequestBody SeatRequestDto seatRequestDto
  ) {
    seatService.updateFlightWholeSeatPrice(flightId, seatRequestDto);
    return ApiResponse.ok("좌석 가격 수정 성공");
  }

}
