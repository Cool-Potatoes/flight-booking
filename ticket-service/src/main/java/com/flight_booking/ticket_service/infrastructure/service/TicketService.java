package com.flight_booking.ticket_service.infrastructure.service;

import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.ticket_service.application.service.BookingService;
import com.flight_booking.ticket_service.application.service.FlightService;
import com.flight_booking.ticket_service.application.service.PaymentService;
import com.flight_booking.ticket_service.application.service.UserService;
import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.domain.model.TicketStateEnum;
import com.flight_booking.ticket_service.domain.repository.TicketRepository;
import com.flight_booking.ticket_service.infrastructure.Redis.RedisLock;
import com.flight_booking.ticket_service.infrastructure.messaging.TicketKafkaSender;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
public class TicketService {

  private final TicketRepository ticketRepository;
  private final TicketKafkaSender ticketKafkaSender;
  private final RedisLock redisLock;
  private final BookingService bookingservice;
  private final FlightService flightService;
  private final PaymentService paymentService;
  private final UserService userService;


  @Transactional
  public TicketResponseDto createTicket(TicketRequestDto ticketRequestDto) {

    Ticket ticket = Ticket.builder().bookingId(ticketRequestDto.bookingId())
        .passengerId(ticketRequestDto.passengerId()).seatId(ticketRequestDto.seatId())
        .state(TicketStateEnum.BOOKED).build();

    Ticket savedTicket = ticketRepository.save(ticket);

    // oldTicket 처리
    if(ticketRequestDto.ticketId() != null){

      Ticket oldTicket = ticketRepository.findByTicketIdAndIsDeletedFalse(ticketRequestDto.ticketId())
          .orElseThrow(RuntimeException::new);

      oldTicket.updateState(TicketStateEnum.REFUND);

      redisLock.unlock(oldTicket.getSeatId());
    }


//    bookingservice.getBooking(ticket.getBookingId());
    // bookingID는 가지고 있ㄴ으니까? 상태를 굳이 체크해야하나?
    // booking_refund_complete 는 이미 비동기로 이거 하기 전에 처리해농흠
    // 여기서 booking 조회해 온 다음에 status가 create면
    // 여기서 booking 조회해 온 다음에 status가 create가 아니면 booking status를 complete로
    // ticket을 찾아올때 booking id랑 상태가 환불 진행중인거 찾아오면 될듯?
    // 찾아와서 상태 업데이트
    // todo : 다른 kafka 메시지 보내기,
    //  ticket에도 보내서 상태업데이트 + lock 해제
    // 여기서 이메일로 조회한다음에

    // 지금 해야할것은 기존 ticket 상태 환부으로 변경
    // 기존 좌석 true로 바꾸는거
    // lock 해제

    // 새로운 booking 만들때 상태를 create로 만들어놨음(업데이트할때)
    // 1. 여기서 booking 상태 완료로 바꿔주긴 해야함.
    // 어떻게? -> 위에서 booking 아이디 가지고있음
    // 2. 기존 ticket 상태 환불로 변경
    // 기존 ticket을 어떻게 찾냐? ->
    // 3. true로 바꾸는거랑 lock 해제 : booking의 상태가 create인것을 찾으면 새로운 booking인데, 거기에 새로 들어갈 seatid -> 락 된 seat 찾아서 풀수있음, true로 바꿀수있음
    // 근데 create인거를 찾으면 안될듯 왜냐면 다른거도 create도 있을수있음

    ////// -> 상태로 체크하는거는 ㄴ

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

    UUID seatId = ticket.getSeatId();
    boolean lockAcquired = redisLock.tryLock(seatId, 30, TimeUnit.SECONDS);
    if (!lockAcquired) {
      throw new RuntimeException("다른 사용자가 해당 좌석을 예약 중입니다.");
    }

    if (!validateSeatAvailable(userDetails, ticketRequestDto.passengerRequestDto().seatId())) {
      throw new RuntimeException("해당 좌석은 예약이 불가능한 상태입니다.");
    }

    ticket.updateState(TicketStateEnum.PROCESS_REFUND);

    ticketKafkaSender.sendMessage("seat-calculate-difference-and-refund-topic",
        ticket.getTicketId().toString(),
        SeatCalculateDifferenceAndRefundRequestDto.from(userDetails, ticket.getTicketId(),
            ticket.getBookingId(),
            ticket.getSeatId(), ticket.getPassengerId(),
            ticketRequestDto.passengerRequestDto()), StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());

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
//      sendKafkaMessagesForUpdateStatusToRefund(ticket);
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

  private Long getPaymentFair(Ticket ticket, CustomUserDetails userDetails) {

    // 환불을 해주기 위해 bookingId로 찾은 결제되어있는 금액 리턴
    return paymentService.getPaymentFairByBookingId(userDetails.email(), userDetails.role(),
        ticket.getBookingId());
  }


  private Ticket validateTicketForCancellation(UUID ticketId) {

    Ticket ticket = getTicketById(ticketId);

    if (!ticket.getState().equals(TicketStateEnum.BOOKED)) {
      throw new RuntimeException("취소 불가");
    }

    return ticket;
  }

  private Boolean checkFlightCancellable(String email, String role, UUID seatId) {

    return flightService.checkFlightStatusBySeatId(email, role, seatId);
  }

  private Boolean ProcessRefund(Ticket ticket, CustomUserDetails userDetails) {

    Long paymentFair = getPaymentFair(ticket, userDetails);

    return userService.RefundMileage(userDetails.email(), userDetails.role(), userDetails.email(),
        paymentFair);
  }

  private Boolean validateSeatAvailable(CustomUserDetails userDetails, UUID seatId) {

    return flightService.getSeatIsAvailable(userDetails.email(), userDetails.role(), seatId);
  }
}
