package com.flight_booking.payment_service.application.service;

import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.ProcessPaymentRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.domain.repository.PaymentRepository;
import com.flight_booking.payment_service.presentation.request.UpdateFareRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final KafkaTemplate<String, ApiResponse<?>> kafkaTemplate;

  @Transactional
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

    Payment savedPayment = paymentRepository.save(payment);

    // TODO 마일리지 확인 및 차감 -> 성공적으로 이루어지면 status 변경 -> 탑승객 생성
    kafkaTemplate.send("payment-mile-topic", savedPayment.getPaymentId().toString(),
        ApiResponse.ok(new UserRequestDto(paymentRequestDto.email(), // user email
            paymentRequestDto.fare(),
            paymentRequestDto.bookingId(),
                1000L), // TODO mileage 몇으로?
            "message from createPayment"));

    return PaymentResponseDto.from(payment);
  }

  public PaymentResponseDto getPayment(UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    // TODO 사용자 권한검증

    return PaymentResponseDto.from(payment);
  }

  public PagedModel<PaymentResponseDto> getProductsPage(
      List<UUID> uuidList, Predicate predicate, Pageable pageable) {

    Page<PaymentResponseDto> paymentResponseDtoPage
        = paymentRepository.findAll(uuidList, predicate, pageable);

    return PaymentResponseDto.from(paymentResponseDtoPage);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public PaymentResponseDto updateFare(UpdateFareRequestDto updateFareRequestDto, UUID paymentId) {

    Payment payment = paymentRepository.findByPaymentIdWithLock(paymentId)
        .orElseThrow(() -> new ApiException("존재하지 않는 paymentId"));

    if (!payment.getBookingId().equals(updateFareRequestDto.bookingId())) {
      throw new ApiException("예약 아이디를 다시 확인해주세요.");
    }

    // 기존 결제 금액 확인
    Long currentFare = payment.getFare();

    // 충돌 여부 확인
    if (!currentFare.equals(updateFareRequestDto.previousFare())) {
      throw new ApiException("결제 금액 충돌 발생: 현재 금액이 변경되었습니다. "
          + "현재 금액: " + currentFare + ", 요청 전 금액: " + updateFareRequestDto.previousFare());
    }

    // TODO 사용자 권한검증
    // TODO 마일리지 확인 및 증감

    Payment updatedPayment = payment.updateFare(updateFareRequestDto.newFare()); // TODO updatedBy

    return PaymentResponseDto.from(updatedPayment);
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

  @Transactional
  public PaymentResponseDto processPayment(ProcessPaymentRequestDto processPaymentRequestDto) {

    Payment payment = paymentRepository.findByBookingId(processPaymentRequestDto.bookingId()).orElseThrow();

    payment.updateFare(processPaymentRequestDto.fair());
    payment.updateStatus(processPaymentRequestDto.statusEnum());

    kafkaTemplate.send("payment-complete-topic", payment.getPaymentId().toString(),
        ApiResponse.ok(new BookingProcessRequestDto(processPaymentRequestDto.bookingId(),
                BookingStatusEnum.BOOKING_COMPLETE), // TODO mileage 몇으로?
            "message from updateUserMileage"));

    return PaymentResponseDto.from(payment);
  }
}
