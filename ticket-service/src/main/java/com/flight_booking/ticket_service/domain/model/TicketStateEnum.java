package com.flight_booking.ticket_service.domain.model;

public enum TicketStateEnum {

  BOOKED("예약됨"),
  CANCEL_PENDING("취소 대기 중"),
  CANCELLED("취소됨"),
  REFUND("환불됨"),
  CANNOT_CANCEL("취소 불가"),
  CHECKED_IN("체크인 완료됨"),
  BOARDED("탑승 완료됨"),
  COMPLETED("여행 완료됨"),
  EXPIRED("만료됨"),
  NO_SHOW("미탑승");

  private final String description;

  TicketStateEnum(String state) {
    this.description = state;
  }

  public String getDescription() {
    return description;
  }
}
