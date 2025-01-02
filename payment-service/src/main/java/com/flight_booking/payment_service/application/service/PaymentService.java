package com.flight_booking.payment_service.application.service;

import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.domain.model.PaymentStatusEnum;
import com.flight_booking.payment_service.domain.repository.PaymentRepository;
import com.flight_booking.payment_service.presentation.request.PaymentRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {

    // bookindId로 이미 결제시도한게 있는지 검증
    if (paymentRepository.existsByBookingId(paymentRequestDto.bookingId())) {
      throw new ApiException("중복된 결제 시도");
    }

    // TODO createdBy 넣어주기 or Auditing
    Payment payment = Payment.builder()
        .bookingId(paymentRequestDto.bookingId())
        .fare(paymentRequestDto.fare())
        .status(PaymentStatusEnum.PENDING)
        .build();

    paymentRepository.save(payment);

    // TODO 마일리지 확인 및 차감 -> 성공적으로 이루어지면 status 변경 -> 탑승객 생성

    return PaymentResponseDto.from(payment);
  }

  @Transactional(readOnly = true)
  public PaymentResponseDto getPayment(UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    // TODO 사용자 권한검증

    return PaymentResponseDto.from(payment);
  }

  @Transactional(readOnly = true)
  public PagedModel<PaymentResponseDto> getProductsPage(
      List<UUID> uuidList, Predicate predicate, Pageable pageable) {

    Page<PaymentResponseDto> paymentResponseDtoPage
        = paymentRepository.findAll(uuidList, predicate, pageable);

    return PaymentResponseDto.from(paymentResponseDtoPage);
  }

  @Transactional
  public PaymentResponseDto updateFare(PaymentRequestDto paymentRequestDto, UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    if (!payment.getBookingId().equals(paymentRequestDto.bookingId())) {
      throw new ApiException("예약 아이디를 다시 확인해주세요.");
    }

    // TODO 사용자 권한검증
    // TODO 마일리지 확인 및 증감

    Payment updatedPayment = payment.updateFare(paymentRequestDto.fare()); // TODO updatedBy

    return PaymentResponseDto.from(payment);
  }

  @Transactional
  public void deletePayment(UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    payment.delete(); // TODO deletedBy
  }


  /**
   * paymentId에 해당하는 payment 조회 메서드
   *
   * @param paymentId
   * @return Payment 객체
   * @throws ApiException 결제 정보가 존재하지 않거나 삭제된 경우 예외처리
   */
  private Payment getPaymentById(UUID paymentId) {
    Payment payment = paymentRepository.findByPaymentIdAndIsDeletedFalse(paymentId)
        .orElseThrow(() -> new ApiException("존재하지 않는 paymentId"));
    return payment;
  }

}
