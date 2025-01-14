package com.flight_booking.ticket_service.application.service;

import com.flight_booking.common.application.dto.BookingUpdateRequestDto;
import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.application.dto.TicketUpdateStatusRequestDto;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.domain.model.TicketStateEnum;
import com.flight_booking.ticket_service.domain.repository.TicketRepository;
import com.flight_booking.ticket_service.infrastructure.messaging.TicketKafkaSender;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
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
public class TicketService {

  private final TicketRepository ticketRepository;
  private final TicketKafkaSender ticketKafkaSender;

  @Transactional
  public TicketResponseDto createTicket(TicketRequestDto ticketRequestDto) {

    // 예약에 해당하는 항공권이 이미 있는지 확인
    if (ticketRepository.existsByBookingId(ticketRequestDto.bookingId())) {
      throw new RuntimeException("해당 예약에 대한 항공권이 이미 존재합니다.");
    }

    Ticket ticket = Ticket.builder()
        .bookingId(ticketRequestDto.bookingId())
        .passengerId(ticketRequestDto.passengerId())
        .seatId(ticketRequestDto.seatId())
        .state(TicketStateEnum.BOOKED)
        .build();

    Ticket savedTicket = ticketRepository.save(ticket);

    return TicketResponseDto.from(savedTicket);
  }

  @Transactional(readOnly = true)
  public TicketResponseDto getTicket(UUID ticketId) {

    Ticket ticket = getTicketById(ticketId);

    return TicketResponseDto.from(ticket);
  }

  @Transactional(readOnly = true)
  public PagedModel<TicketResponseDto> getTicketsPage(
      String email, List<UUID> uuidList, Predicate predicate, Pageable pageable) {

    Page<TicketResponseDto> ticketResponseDtoPage
        = ticketRepository.findAll(email, uuidList, predicate, pageable);

    return new PagedModel<>(ticketResponseDtoPage);
  }

  @Transactional
  public TicketResponseDto updateTicket(UUID ticketId, TicketUpdateRequestDto ticketRequestDto,
      CustomUserDetails userDetails) {

    String userEmail = userDetails.email();

    Ticket ticket = getTicketById(ticketId);

    if (!ticketRequestDto.bookingId().equals(ticket.getBookingId())) {
      throw new RuntimeException("예약 ID와 항공권이 일치하지 않습니다.");
    }
    if (!ticketRequestDto.passengerId().equals(ticket.getPassengerId())) {
      throw new RuntimeException("항공권에 해당하는 탑승ID가 아닙니다.");
    }

    // TODO 변경하려는 seatID에 해당하는 seat의 isAvailable을 조회하며 Lock
    // TODO 변경된 좌석의 금액이 원래 좌석과 다르면 그 차이만큼 마일리지 증감
    // TODO 기내식, 위탁수화물 등 변경
    // TODO 수정 성공시 이전 예약 좌석은 다시 Available로 변경
    //  -> 해결!
    ticketKafkaSender.sendMessage(
        "booking-update-topic",
        ticket.getTicketId().toString(),
        new BookingUpdateRequestDto(
            ticketId,
            ticket.getBookingId(), ticketRequestDto.passengerRequestDtos(),
            userEmail)
    );

    // TODO updatedBy
    // ticket.update(ticketRequestDto.seatId());

    return TicketResponseDto.from(ticket);
  }

  @Transactional
  public void cancelTicket(UUID ticketId, String username) {

    Ticket ticket = getTicketById(ticketId);

    if (!ticket.getState().equals(TicketStateEnum.BOOKED)) {
      throw new RuntimeException("취소 불가");
    }

    // TODO (kafka 비동기 처리) 삭제 가능한지 확인 Flight 상태 확인 -> 마일리지 반환 -> Ticket state update
    ticketKafkaSender.sendMessage(
        "flight-cancel-availability-topic",
        ticket.getTicketId().toString(),
        new FlightCancelRequestDto(
            username,
            ticket.getTicketId(), ticket.getBookingId(),
            ticket.getPassengerId(), ticket.getSeatId())
    );

    ticket.updateState(TicketStateEnum.CANCEL_PENDING);
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
}
