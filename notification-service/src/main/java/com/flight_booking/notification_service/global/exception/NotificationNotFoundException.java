package com.flight_booking.notification_service.global.exception;

public class NotificationNotFoundException extends RuntimeException {
  public NotificationNotFoundException(String message) {
    super(message);
  }
}
