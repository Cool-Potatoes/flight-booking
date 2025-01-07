package com.flight_booking.ticket_service.domain.repository;

import com.flight_booking.ticket_service.domain.model.Ticket;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

  boolean existsByBookingId(UUID uuid);

  Optional<Ticket> findByIdIsDeletedFalse(UUID ticketId);
}
