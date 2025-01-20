package com.flight_booking.payment_service.application.service.user;

import com.flight_booking.common.application.dto.PaymentRetryRequestDto;

public interface UserService {

  boolean updateMileage(PaymentRetryRequestDto requestDto);
}
