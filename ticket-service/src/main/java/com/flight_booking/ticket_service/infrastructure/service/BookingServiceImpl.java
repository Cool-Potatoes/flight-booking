package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.ticket_service.application.service.BookingService;
import com.flight_booking.ticket_service.infrastructure.feign.BookingClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

  private final BookingClient bookingClient;

  @Override
  public void getBooking(String email, String role, UUID bookingId) {

    bookingClient.getBooking(email, role, bookingId);
  }
}
