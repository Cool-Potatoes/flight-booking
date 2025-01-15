package com.flight_booking.ticket_service.infrastructure.feign;

import com.flight_booking.common.presentation.dto.BookingRequestDto;
import com.flight_booking.common.presentation.dto.BookingResponseDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "booking-service")
public interface BookingClient {

  @PostMapping("/internal/bookings")
  ApiResponse<BookingResponseDto> createBooking(
      @RequestHeader(value = "X-USER-EMAIL") String email,
      @RequestHeader(value = "X-USER-ROLE") String role,
      @RequestBody BookingRequestDto bookingRequestDto);


}
