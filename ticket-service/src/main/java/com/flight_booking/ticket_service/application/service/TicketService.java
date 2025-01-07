package com.flight_booking.ticket_service.application.service;

import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.domain.repository.TicketRepository;
import com.flight_booking.ticket_service.presentation.dto.TicketRequestDto;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

  private final TicketRepository ticketRepository;


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
        .flightId(ticketRequestDto.flightId())
        .build();

    Ticket savedTicket = ticketRepository.save(ticket);

    return TicketResponseDto.from(savedTicket);
  }

  @Transactional(readOnly = true)
  public TicketResponseDto getTicket(UUID ticketId) {

    Ticket ticket = ticketRepository.findByIdIsDeletedFalse(ticketId)
        .orElseThrow(() -> new RuntimeException("해당하는 항공권이 존재하지 않습니다."));

    return TicketResponseDto.from(ticket);
  }
}
