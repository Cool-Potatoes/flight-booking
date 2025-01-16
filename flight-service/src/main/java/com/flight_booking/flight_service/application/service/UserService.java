package com.flight_booking.flight_service.application.service;

public interface UserService {

  Boolean RefundMileage(String email, String role, String userEmail1, Long paymentFair);
}
