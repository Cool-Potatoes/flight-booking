package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.common.application.dto.BookingStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.PassengerIsdeletedUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.application.dto.PaymentStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.application.dto.TicketUpdateStatusRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.common.presentation.dto.BookingRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.ticket_service.application.service.TicketService;
import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.domain.model.TicketStateEnum;
import com.flight_booking.ticket_service.domain.repository.TicketRepository;
import com.flight_booking.ticket_service.infrastructure.feign.BookingClient;
import com.flight_booking.ticket_service.infrastructure.feign.FlightClient;
import com.flight_booking.ticket_service.infrastructure.feign.PaymentClient;
import com.flight_booking.ticket_service.infrastructure.feign.UserClient;
import com.flight_booking.ticket_service.infrastructure.messaging.TicketKafkaSender;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements
    TicketService {

  private final TicketRepository ticketRepository;
  private final TicketKafkaSender ticketKafkaSender;
  private final BookingClient bookingClient;
  private final FlightClient flightClient;
  private final PaymentClient paymentClient;
  private final UserClient userClient;

  @Transactional
  public TicketResponseDto createTicket(TicketRequestDto ticketRequestDto) {

    Ticket ticket = Ticket.builder().bookingId(ticketRequestDto.bookingId())
        .passengerId(ticketRequestDto.passengerId()).seatId(ticketRequestDto.seatId())
        .state(TicketStateEnum.BOOKED).build();

    Ticket savedTicket = ticketRepository.save(ticket);

    return TicketResponseDto.from(savedTicket);
  }

  @Transactional(readOnly = true)
  public TicketResponseDto getTicket(UUID ticketId) {

    Ticket ticket = getTicketById(ticketId);

    return TicketResponseDto.from(ticket);
  }

  @Transactional(readOnly = true)
  public PagedModel<TicketResponseDto> getTicketsPage(String email, List<UUID> uuidList,
      Predicate predicate, Pageable pageable) {

    Page<TicketResponseDto> ticketResponseDtoPage = ticketRepository.findAll(email, uuidList,
        predicate, pageable);

    return new PagedModel<>(ticketResponseDtoPage);
  }

  @Transactional(readOnly = false)
  public TicketResponseDto updateTicket(UUID ticketId, TicketUpdateRequestDto ticketRequestDto,
      CustomUserDetails userDetails) {

    Ticket ticket = validateTicket(ticketId, ticketRequestDto);

    // 티켓 상태 변경, 환불 진행중
    ticket.updateState(TicketStateEnum.PROCESS_REFUND);

    // 승객들의 정보를 먼저 모두 체크한 후에 처리
    List<PassengerRequestDto> checkedPassengerRequestDtos = validateAndProcessPassengersForRefund(
        ticket,
        ticketRequestDto, userDetails);

    // 승객 모두 체크가 끝난 후, 새로운 예매를 한 번만 호출
    if (!checkedPassengerRequestDtos.isEmpty()) {
      createBooking(ticket, checkedPassengerRequestDtos, userDetails);

      // Kafka 메시지 전송
      sendKafkaMessagesForUpdateStatusToRefund(ticket);
    }

    return TicketResponseDto.from(ticket);
  }

  @Transactional
  public void cancelTicket(UUID ticketId, CustomUserDetails userDetails) {

    Ticket ticket = validateTicketForCancellation(ticketId);

    ticket.updateState(TicketStateEnum.CANCEL_PENDING);

    // TODO (kafka 비동기 처리) 삭제 가능한지 확인 Flight 상태 확인 -> 마일리지 반환 -> Ticket state update

    Boolean isCancellable = checkFlightCancellable(userDetails.email(), userDetails.role(),
        ticket.getSeatId());
    if (!isCancellable) {
      throw new RuntimeException("해당 항공편은 상태 확인 중 문제가 발생했습니다.");
    }

    if (ProcessRefund(ticket, userDetails)) {

      ticket.updateState(TicketStateEnum.CANCELLED);
      sendKafkaMessagesForUpdateStatusToRefund(ticket);
    }
  }

  @Transactional
  public void cancelFail(FlightCancelRequestDto flightCancelRequestDto) {
    Ticket ticket = getTicketById(flightCancelRequestDto.ticketId());

    ticket.updateState(TicketStateEnum.CANNOT_CANCEL);
  }

  private Ticket getTicketById(UUID ticketId) {
    return ticketRepository.findByTicketIdAndIsDeletedFalse(ticketId)
        .orElseThrow(() -> new RuntimeException("해당하는 항공권이 존재하지 않습니다."));
  }

  @Transactional
  public void updateTicketStatus(TicketUpdateStatusRequestDto ticketUpdateRequestDto) {
    Ticket ticket = ticketRepository.findByTicketIdAndIsDeletedFalse(
            ticketUpdateRequestDto.ticketId())
        .orElseThrow(() -> new RuntimeException("해당하는 항공권이 존재하지 않습니다."));

    ticket.updateState(TicketStateEnum.REFUND);
  }


  private Ticket validateTicket(UUID ticketId, TicketUpdateRequestDto ticketRequestDto) {
    Ticket ticket = getTicketById(ticketId);

    if (!ticketRequestDto.bookingId().equals(ticket.getBookingId())) {
      throw new RuntimeException("예약 ID와 항공권이 일치하지 않습니다.");
    }
    if (!ticketRequestDto.passengerId().equals(ticket.getPassengerId())) {
      throw new RuntimeException("항공권에 해당하는 탑승ID가 아닙니다.");
    }
    return ticket;
  }


  private List<PassengerRequestDto> validateAndProcessPassengersForRefund(Ticket ticket,
      TicketUpdateRequestDto ticketRequestDto, CustomUserDetails userDetails) {
    List<PassengerRequestDto> checkedPassengerRequestDtos = new ArrayList<>();

    for (PassengerRequestDto passengerRequestDto : ticketRequestDto.passengerRequestDtos()) {
      boolean isCheckedAndRefund = checkUserMileageAndProcessRefund(ticket,
          passengerRequestDto.seatId(),
          userDetails);

      if (isCheckedAndRefund) {
        checkedPassengerRequestDtos.add(passengerRequestDto);
      }
    }
    return checkedPassengerRequestDtos;
  }

  private void createBooking(Ticket ticket,
      List<PassengerRequestDto> checkedPassengerRequestDtos, CustomUserDetails userDetails) {

    // 새로운 예약 생성
    bookingClient.createBooking(userDetails.email(), userDetails.role(),
        new BookingRequestDto(checkedPassengerRequestDtos));

    // 티켓 상태를 환불 완료로 변경
    ticket.updateState(TicketStateEnum.REFUND);
  }

  private void sendKafkaMessagesForUpdateStatusToRefund(Ticket ticket) {

    ticketKafkaSender.sendMessage("booking-status-update-refund-topic",
        ticket.getTicketId().toString(),
        new BookingStatusUpdateRefundRequestDto(ticket.getBookingId(),
            BookingStatusEnum.BOOKING_REFUND_COMPLETE),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

    ticketKafkaSender.sendMessage("seat-availability-update-true-topic",
        ticket.getTicketId().toString(),
        new SeatAvailabilityUpdateTrueRequestDto(ticket.getSeatId(), true),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

    ticketKafkaSender.sendMessage("passenger-isdeleted-update-true-topic",
        ticket.getTicketId().toString(),
        new PassengerIsdeletedUpdateTrueRequestDto(ticket.getPassengerId(), true),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

    // payment는 bookingId 전송해서 찾아서 처리
    ticketKafkaSender.sendMessage("payment-status-update-refund-topic",
        ticket.getTicketId().toString(),
        new PaymentStatusUpdateRefundRequestDto(ticket.getBookingId(),
            PaymentStatusEnum.REFUND_COMPLETE),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

  }

  private Boolean checkUserMileageAndProcessRefund(Ticket ticket, UUID newSeatId,
      CustomUserDetails userDetails) {

    Long seatPrice = updateSeatAvailableFalseAndGetSeatPrice(newSeatId, userDetails);
    Long paymentFair = getPaymentFair(ticket, userDetails);

    // 예약할 좌석의 가격과 환블해줄 가격의 차이 계산
    Long difference = seatPrice - paymentFair;

    // 마일리지 체크 후 환불 진행

    Boolean isSuccess = checkAndRefundMileage(userDetails, difference, paymentFair);

    return isSuccess;
  }

  private Boolean checkAndRefundMileage(CustomUserDetails userDetails, Long difference, Long paymentFair) {

    return userClient.checkAndRefundMileage(userDetails.email(),
        userDetails.role(), userDetails.email(), difference, paymentFair);
  }

  private Long updateSeatAvailableFalseAndGetSeatPrice(UUID newSeatId,
      CustomUserDetails userDetails) {

    // 예약할 좌석의 available을 false로 바꾸고 해당 좌석의 요금 리턴
    ApiResponse<Long> bookingResponse = flightClient.updateSeatAvailableFalseAndGetSeatPrice(
        userDetails.email(), userDetails.role(), newSeatId);

    return bookingResponse.getData();
  }

  private Long getPaymentFair(Ticket ticket, CustomUserDetails userDetails) {

    // 환불을 해주기 위해 bookingId로 찾은 결제되어있는 금액 리턴
    ApiResponse<Long> paymentResponse = paymentClient.getPaymentFairByBookingId(userDetails.email(),
        userDetails.role(), ticket.getBookingId());

    return paymentResponse.getData();
  }


  private Ticket validateTicketForCancellation(UUID ticketId) {
    Ticket ticket = getTicketById(ticketId);

    if (!ticket.getState().equals(TicketStateEnum.BOOKED)) {
      throw new RuntimeException("취소 불가");
    }

    return ticket;
  }

  private Boolean checkFlightCancellable(String email, String role, UUID seatId) {

    ApiResponse<Boolean> response = flightClient.checkFlightStatusBySeatId(email, role, seatId);

    return response.getData();
  }

  private Boolean ProcessRefund(Ticket ticket, CustomUserDetails userDetails) {

    Long paymentFair = getPaymentFair(ticket, userDetails);

    ApiResponse<Boolean> userResponse = userClient.RefundMileage(userDetails.email(),
        userDetails.role(), userDetails.email(), paymentFair);

    return userResponse.getData();
  }


}
