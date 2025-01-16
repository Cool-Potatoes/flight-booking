package com.flight_booking.ticket_service.application.service;

import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.application.dto.TicketUpdateStatusRequestDto;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.transaction.annotation.Transactional;

public interface TicketService {

  @Transactional
  TicketResponseDto createTicket(TicketRequestDto ticketRequestDto);

  @Transactional(readOnly = true)
  TicketResponseDto getTicket(UUID ticketId);

  @Transactional(readOnly = true)
  PagedModel<TicketResponseDto> getTicketsPage(String email, List<UUID> uuidList,
      Predicate predicate, Pageable pageable);

  @Transactional(readOnly = false)
  TicketResponseDto updateTicket(UUID ticketId, TicketUpdateRequestDto ticketRequestDto,
      CustomUserDetails userDetails);

  @Transactional
  void cancelTicket(UUID ticketId, CustomUserDetails userDetails);

  @Transactional
  void cancelFail(FlightCancelRequestDto flightCancelRequestDto);

  @Transactional
  void updateTicketStatus(TicketUpdateStatusRequestDto ticketUpdateRequestDto);
}
