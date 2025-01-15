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
  ApiResponse<Long> updateSeatAvailableFalseAndGetSeatPrice(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID seatId);

  @GetMapping("/internal/flights/{seatId}")
  ApiResponse<Boolean> checkFlightStatus(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @PathVariable UUID seatId);

//  @PostMapping
//  ApiResponse<BookingResponseDto> createBooking(
//      @RequestBody BookingRequestDto bookingRequestDto,
//      @AuthenticationPrincipal CustomUserDetails userDetails) {
//
//    return ApiResponse.ok(
//        bookingService.createBooking(bookingRequestDto, userDetails.getUsername()),
//        "예매 생성 성공, 결제 대기중");
//  }
}
