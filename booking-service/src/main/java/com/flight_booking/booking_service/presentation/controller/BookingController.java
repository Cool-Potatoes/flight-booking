package com.flight_booking.booking_service.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
import com.flight_booking.booking_service.presentation.request.BookingUpdateRequestDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.querydsl.core.types.Predicate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping
  public ApiResponse<?> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {

    return ApiResponse.ok(bookingService.createBooking(bookingRequestDto), "예매 생성 성공, 결제 대기중");
  }

  @GetMapping
  public ApiResponse<?> getBookings(@QuerydslPredicate(root = Booking.class) Predicate predicate,
      Pageable pageable) {

    PagedModel<BookingResponseCustomDto> bookings = bookingService.getBookings(predicate, pageable);

    if (bookings.getContent().isEmpty()) {
      return ApiResponse.noContent();
    }

    return ApiResponse.ok(bookings, "예매 목록 조회 성공");
  }

  @GetMapping("/{bookingId}")
  public ApiResponse<?> getBooking(@PathVariable UUID bookingId) {

    return ApiResponse.ok(bookingService.getBooking(bookingId), "예매 상세 조회 성공");
  }

  @PatchMapping("/{bookingId}")
  public ApiResponse<?> updateBooking(@PathVariable UUID bookingId,
      @RequestBody BookingUpdateRequestDto bookingRequestDto) {

    return ApiResponse.ok(bookingService.updateBooking(bookingId, bookingRequestDto), "예매 수정 성공");
  }

  @DeleteMapping("/{bookingId}")
  public ApiResponse<?> deleteBooking(@PathVariable UUID bookingId) {

    bookingService.deleteBooking(bookingId);

    return ApiResponse.ok("예매 삭제 성공");
  }


}
