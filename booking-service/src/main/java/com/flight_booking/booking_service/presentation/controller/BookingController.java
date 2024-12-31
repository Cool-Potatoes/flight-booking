package com.flight_booking.booking_service.presentation.controller;

import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.booking_service.common.response.ApiResponse;
import com.flight_booking.booking_service.presentation.request.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping()
  public ApiResponse<?> createBooking(@RequestBody BookingRequest bookingRequest){

    return ApiResponse.ok(bookingService.createBooking(bookingRequest));
  }
}
