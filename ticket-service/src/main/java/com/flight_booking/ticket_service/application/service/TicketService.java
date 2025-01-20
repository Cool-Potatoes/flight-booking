package com.flight_booking.ticket_service.application.service;

import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.domain.model.TicketStateEnum;
import com.flight_booking.ticket_service.domain.repository.TicketRepository;
import com.flight_booking.ticket_service.infrastructure.messaging.TicketKafkaSender;
import com.flight_booking.ticket_service.infrastructure.redis.RedisLock;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    if (ticketRequestDto.ticketId() != null) {

      Ticket oldTicket = ticketRepository.findByTicketIdAndIsDeletedFalse(
              ticketRequestDto.ticketId())
          .orElseThrow(RuntimeException::new);

      oldTicket.updateState(TicketStateEnum.REFUND);

    }

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

    Ticket ticket = getTicketIfValid(ticketId, ticketRequestDto);

    UUID seatId = ticketRequestDto.passengerRequestDto().seatId();
    boolean lockAcquired = redisLock.tryLock(seatId, 300, TimeUnit.SECONDS);
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

  private Ticket getTicketIfValid(UUID ticketId, TicketUpdateRequestDto ticketRequestDto) {

    Ticket ticket = getTicketById(ticketId);

    if(ticket.getState() == TicketStateEnum.PROCESS_REFUND){
      throw new RuntimeException("이미 환불중인 티켓 중입니다.");
    }

    if (ticket.getState() == TicketStateEnum.PROCESS_REFUND) {
      throw new RuntimeException("이미 환불중인 티켓 중입니다.");
    }

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

  // CircuitBreaker를 따로 클래스 분리 하려고 했지만 내부 로직 중에서 다른 서비스에서 호출해야만 하는 부분이 있어서 롤백.
  @CircuitBreaker(name = "ticketService-ProcessRefund", fallbackMethod = "fallbackProcessRefund")
  private Boolean ProcessRefund(Ticket ticket, CustomUserDetails userDetails) {

    Long paymentFair = getPaymentFair(ticket, userDetails);

    return userService.RefundMileage(userDetails.email(), userDetails.role(), userDetails.email(),
        paymentFair);
  }

  @CircuitBreaker(name = "ticketService-validateSeatAvailable", fallbackMethod = "fallbackValidateSeatAvailable")
  private Boolean validateSeatAvailable(CustomUserDetails userDetails, UUID seatId) {

    return flightService.getSeatIsAvailable(userDetails.email(), userDetails.role(), seatId);
  }

  private Boolean fallbackProcessRefund(Throwable t) {
    log.warn("Refund failed. Reason: {}", t.getMessage());
    // 실패 트랜잭션
    // feign 호출하면서 try catch 하면 되는데, 서킷브레이커하면 될듯
    // 모니터링보다는 테스트?
    return false;
  }

  private Boolean fallbackValidateSeatAvailable(Throwable t) {
    log.warn("Seat availability check failed. Defaulting to unavailable.");
    return false;
  }
}
