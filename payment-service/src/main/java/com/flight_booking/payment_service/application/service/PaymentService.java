package com.flight_booking.payment_service.application.service;

import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingRefundRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundFromTicketRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundProcessRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.application.dto.PaymentStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.ProcessTicketPaymentRequestDto;
import com.flight_booking.common.application.dto.UserRefundTicketRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.common.presentation.dto.NotificationRequest;
import com.flight_booking.payment_service.application.service.user.UserService;
import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.domain.repository.PaymentRepository;
import com.flight_booking.payment_service.infrastructure.messaging.PaymentKafkaSender;
import com.flight_booking.payment_service.presentation.request.UpdateFareRequestDto;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentKafkaSender paymentKafkaSender;
  private final UserService userService;

  @Transactional
  public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {

    // bookindId로 이미 결제시도한게 있는지 검증
    if (paymentRepository.existsByBookingId(paymentRequestDto.bookingId())) {
      throw new ApiException("중복된 결제 시도");
    }

    Payment payment = Payment.builder()
        .bookingId(paymentRequestDto.bookingId())
        .fare(paymentRequestDto.fare())
        .status(PaymentStatusEnum.PENDING)
        .build();

    Payment savedPayment = paymentRepository.save(payment);

    paymentKafkaSender.sendMessage(
        "user-update-mileage-topic",
        savedPayment.getPaymentId().toString(),
        new UserRequestDto(
            paymentRequestDto.ticketId(),
            paymentRequestDto.email(), // user email
            savedPayment.getFare(),
            savedPayment.getPaymentId()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );

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

    Payment updatedPayment = payment.updateFare(updateFareRequestDto.newFare());

    return PaymentResponseDto.from(updatedPayment);
  }

  @Transactional
  public void deletePayment(UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    payment.delete();
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
  public void processPaymentSuccess(PaymentRefundProcessRequestDto paymentRefundProcessRequestDto) {

    Payment payment = getPaymentById(paymentRefundProcessRequestDto.paymentId());

    Payment updatedPayment = payment.updateStatus(PaymentStatusEnum.PAYED);

    paymentKafkaSender.sendMessage(
        "booking-complete-topic",
        updatedPayment.getBookingId().toString(),
        new BookingProcessRequestDto(paymentRefundProcessRequestDto.ticketId(),
            updatedPayment.getBookingId(),
            paymentRefundProcessRequestDto.email()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );

  }

  @Transactional
  public void processPaymentFail(PaymentRefundProcessRequestDto requestDto) {

    Payment payment = getPaymentById(requestDto.paymentId());

    Payment updatedPayment = payment.updateStatus(PaymentStatusEnum.PAYED_FAIL);

    paymentKafkaSender.sendMessage(
        "booking-fail-topic",
        updatedPayment.getBookingId().toString(),
        new BookingProcessRequestDto(requestDto.ticketId(), updatedPayment.getBookingId(),
            requestDto.email()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );

    paymentKafkaSender.sendMessage(
        "user-create-notification-topic",
        requestDto.email(),
        new NotificationRequest(null, "from System", requestDto.email(),
            "결제 실패 알림", "마일리지 부족으로 인한 결제 실패 알림."),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());

  }

  @Transactional
  public void processPaymentRefundFail(
      PaymentRefundProcessRequestDto paymentRefundProcessRequestDto) {

    Payment payment = getPaymentById(paymentRefundProcessRequestDto.paymentId());

    Payment refundPayment = payment.updateStatus(PaymentStatusEnum.REFUND_FAIL);

    paymentKafkaSender.sendMessage(
        "booking-refund-fail-topic",
        refundPayment.getBookingId().toString(),
        new BookingProcessRequestDto(paymentRefundProcessRequestDto.ticketId(),
            refundPayment.getBookingId(),
            paymentRefundProcessRequestDto.email()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );
  }

  public Long getPaymentFairByBookingId(UUID bookingId) {

    Payment payment = getPaymentByBookingIdAndIsDeletedFalse(bookingId);

    return payment.getFare();
  }

  private Payment getPaymentByBookingIdAndIsDeletedFalse(UUID bookingId) {

    return paymentRepository.findPaymentByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(() -> new ApiException("존재하지 않는 bookingId"));
  }

  @Transactional(readOnly = false)
  public void paymentStatusUpdateRefund(PaymentStatusUpdateRefundRequestDto requestDto) {

    Payment payment = getPaymentEntityByBookingId(requestDto.bookingId());

    payment.updateStatus(requestDto.paymentStatusEnum());
  }

  @Transactional
  public void updatePaymentState(PaymentStatusEnum paymentStatusEnum, UUID paymentId) {

    Payment payment = getPaymentById(paymentId);

    payment.updateStatus(paymentStatusEnum);
  }

  public boolean retryPayment(PaymentRetryRequestDto requestDto) {
    return userService.updateMileage(requestDto);
  }

  public void sendPaymentDLQ(PaymentRetryRequestDto requestDto) {
    paymentKafkaSender.sendMessage(
        "payment-dlq-topic", requestDto.email(), requestDto,
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());
  }

  public void sendPaymentRetry(PaymentRetryRequestDto requestDto) {
    paymentKafkaSender.sendMessage(
        "payment-retry-topic", requestDto.paymentId().toString(), requestDto,
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName()
    );
  }

  public void sendPayedMessage(PaymentRetryRequestDto dto) {
    NotificationRequest request = new NotificationRequest(
        null, "from System", dto.email(),
        "결제가 완료되었습니다.",
        "PaymentId: " + dto.paymentId() + ", 결제 금액: " + dto.fare() + " 원이 결제 되었습니다."
    );

    createNotification(request);
  }

  private Payment getPaymentEntityByBookingId(UUID bookingId) {
    return paymentRepository.findPaymentByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(() -> new ApiException("존재하지 않는 bookingId"));
  }

  private void createNotification(NotificationRequest notificationRequest) {
    paymentKafkaSender.sendMessage(
        "user-create-notification-topic",
        notificationRequest.receiverEmail(),
        notificationRequest,
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());
  }
}
