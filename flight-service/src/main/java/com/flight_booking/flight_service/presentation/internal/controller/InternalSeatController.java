package com.flight_booking.flight_service.presentation.internal.controller;

import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.SeatService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/seats")
@RequiredArgsConstructor
public class InternalSeatController {

  private final SeatService seatService;

  @GetMapping("/{seatId}")
  public ApiResponse<?> updateSeatAvailableFalseAndGetSeatPrice(
      @PathVariable UUID seatId
  ) {

    Long newSeatPrice = seatService.updateSeatAvailableFalseAndGetSeatPrice(seatId);

    return ApiResponse.ok(newSeatPrice, "좌석 요금 반환 성공, 상태 변경 완료");
  }
}
