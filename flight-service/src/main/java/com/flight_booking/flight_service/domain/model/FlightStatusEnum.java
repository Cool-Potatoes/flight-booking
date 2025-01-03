package com.flight_booking.flight_service.domain.model;

public enum FlightStatusEnum {
  SCHEDULED, // 출발 전
  DELAYED, // 연착
  CANCELLED, // 취소
  LANDED //도착
}
