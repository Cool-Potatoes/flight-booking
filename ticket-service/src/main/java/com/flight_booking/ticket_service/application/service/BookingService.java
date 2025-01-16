package com.flight_booking.ticket_service.application.service;

import com.flight_booking.common.presentation.dto.BookingRequestDto;

public interface BookingService {

  void createBooking(String email, String role,
      BookingRequestDto bookingRequestDto);
}
