package com.flight_booking.ticket_service.infrastructure.feign;

import com.flight_booking.common.presentation.global.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "flight-service")
public interface FlightClient {

  @GetMapping("/internal/seats/{seatId}")
  Long updateSeatAvailableFalseAndGetSeatPrice(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID seatId);

  @GetMapping("/internal/flights/{seatId}")
  Boolean checkFlightStatusBySeatId(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID seatId);

  @GetMapping("/internal/seats/available/{seatId}")
  Boolean getSeatIsAvailable(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID seatId);

}
