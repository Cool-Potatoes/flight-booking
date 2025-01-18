package com.flight_booking.booking_service.presentation.internal.controller;

import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.common.presentation.dto.BookingRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bookings")
public class InternalBookingController {

  private final BookingService bookingService;

  @PostMapping
  public ApiResponse<?> createBooking(@RequestBody BookingRequestDto bookingRequestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ApiResponse.ok(
        bookingService.createBooking(bookingRequestDto, userDetails.getUsername()),
        "예매 생성 성공, 결제 대기중");
  }
}
