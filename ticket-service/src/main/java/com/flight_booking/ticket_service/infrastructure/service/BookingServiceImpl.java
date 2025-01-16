package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.common.presentation.dto.BookingRequestDto;
import com.flight_booking.ticket_service.application.service.BookingService;
import com.flight_booking.ticket_service.infrastructure.feign.BookingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

  private final BookingClient bookingClient;

  @Override
  public void createBooking(String email, String role,
      BookingRequestDto bookingRequestDto) {

    // 추가 로직 있으면 여기서 하고

    // FeignClient 호출
    bookingClient.createBooking(email, role, bookingRequestDto);
  }
}
