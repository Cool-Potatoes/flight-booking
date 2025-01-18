package com.flight_booking.ticket_service.application.service;

import java.util.UUID;

public interface PaymentService {

  Long getPaymentFairByBookingId(String email, String role, UUID bookingId);
}
