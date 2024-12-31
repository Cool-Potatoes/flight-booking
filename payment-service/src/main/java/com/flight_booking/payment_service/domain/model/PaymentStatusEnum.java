package com.flight_booking.payment_service.domain.model;

public enum PaymentStatusEnum {

  PENDING("PENDING"),         // 결제 대기중 (결제 요청됨, 처리 전)
  IN_PROGRESS("IN_PROGRESS"), // 결제 진행 중 (결제 처리 중)
  PAYED("PAYED"),             // 결제 완료
  REFUND("REFUND"),           // 환불 완료
  PAYED_FAIL("PAYED_FAIL"),   // 결제 실패
  REFUND_FAIL("REFUND_FAIL"); // 환불 실패

  private final String state;

  PaymentStatusEnum(String state) {
    this.state = state;
  }

  public String getStatus() {
    return this.state;
  }

}
