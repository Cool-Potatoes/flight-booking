package com.flight_booking.ticket_service.application.service;

public interface UserService {


  Boolean checkAndRefundMileage(String email, String role, String email1, Long difference,
      Long paymentFair);

  Boolean RefundMileage(String email, String role, String userEmail1, Long paymentFair);
}
