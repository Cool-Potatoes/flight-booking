package com.flight_booking.booking_service.presentation.controller;

import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.booking_service.common.response.ApiResponse;
import com.flight_booking.booking_service.presentation.request.BookingRequest;
import com.flight_booking.booking_service.presentation.response.BookingResponse;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustom;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping
  public ApiResponse<?> createBooking(@RequestBody BookingRequest bookingRequest) {

    return ApiResponse.ok(bookingService.createBooking(bookingRequest), "예약 성공");
  }

  @GetMapping
  public ApiResponse<?> getBookings(@PageableDefault(page = 0, size = 10, sort = "createdAt",
      direction = Sort.Direction.DESC) Pageable pageable, @RequestParam Integer size) {

    Page<BookingResponseCustom> bookings = bookingService.getBookings(pageable, size);

    if (bookings.isEmpty()) {
      return ApiResponse.noContent();
    }

    return ApiResponse.ok(bookings,"예매 목록 조회 성공");
  }

  @GetMapping("/{bookingId}")
  public ApiResponse<?> getBooking(@RequestParam UUID bookingId) {

    return ApiResponse.ok(bookingService.getBooking(bookingId), "예매 상세 조회 성공");
  }
}
