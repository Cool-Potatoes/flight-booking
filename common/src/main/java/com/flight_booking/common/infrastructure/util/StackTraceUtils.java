package com.flight_booking.common.infrastructure.util;

public class StackTraceUtils {

  // 호출한 메서드의 클래스명 반환
  public static String getCurrentClassName() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    return stackTrace[2].getClassName();
  }

  // 호출한 메서드의 이름 반환
  public static String getCurrentMethodName() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    return stackTrace[2].getMethodName();
  }
}
