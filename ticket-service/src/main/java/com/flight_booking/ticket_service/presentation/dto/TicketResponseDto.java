package com.flight_booking.ticket_service.presentation.dto;

import com.flight_booking.ticket_service.domain.model.Ticket;
import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;

public record TicketResponseDto(
    UUID ticketId,
    UUID bookingId,
    UUID passengerId,
    UUID seatId,
    String ticketState
) {

  @QueryProjection
  public TicketResponseDto(Ticket ticket) {
    this(
        ticket.getTicketId(),
        ticket.getBookingId(),
        ticket.getPassengerId(),
        ticket.getSeatId(),
        ticket.getState().getDescription()
    );
  }

  public static TicketResponseDto from(Ticket ticket) {
    return new TicketResponseDto(ticket);
  }
}