package com.flight_booking.ticket_service.application.service;

import java.util.UUID;

public interface BookingService {

  void getBooking(String email, String role, UUID bookingId);
}
