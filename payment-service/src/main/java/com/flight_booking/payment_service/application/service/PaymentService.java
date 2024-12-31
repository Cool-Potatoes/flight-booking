package com.flight_booking.payment_service.application.service;

import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.domain.model.PaymentStatusEnum;
import com.flight_booking.payment_service.domain.repository.PaymentRepository;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {

    // bookindId로 이미 결제시도한게 있는지 검증
    if (paymentRepository.existsByBookingId(paymentRequestDto.getBookingId())) {
      throw new ApiException("중복된 결제 시도");
    }

    // TODO createdBy 넣어주기 or Auditing
    Payment payment = Payment.builder()
        .bookingId(paymentRequestDto.getBookingId())
        .fare(paymentRequestDto.getFare())
        .status(PaymentStatusEnum.PENDING)
        .build();

    paymentRepository.save(payment);

    // TODO 마일리지 확인 및 차감 -> 성공적으로 이루어지면 status 변경 -> 탑승객 생성

    return new PaymentResponseDto(payment);
  }

  public PaymentResponseDto getPayment(UUID paymentId) {

    Payment payment = paymentRepository.findByPaymentIdAndIsDeletedFalse(paymentId)
        .orElseThrow(() -> new ApiException("존재하지 않는 paymentId"));

    // TODO 사용자 권한검증

    return new PaymentResponseDto(payment);
  }
}
