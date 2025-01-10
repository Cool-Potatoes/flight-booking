package com.flight_booking.common.domain.model;

public enum PaymentStatusEnum {

  PENDING("결제 대기중 (결제 요청됨, 처리 전)"),
  IN_PROGRESS("결제 진행 중 (결제 처리 중)"),
  PAYED("결제 완료"),
  REFUND("환불 완료"),
  PAYED_FAIL("결제 실패"),
  REFUND_FAIL("환불 실패");

  private final String description;

  PaymentStatusEnum(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
