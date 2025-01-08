package com.flight_booking.ticket_service.infrastructure.repository;

import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketRepositoryCustom {

  Page<TicketResponseDto> findAll(
      String email, List<UUID> uuidList, Predicate predicate, Pageable pageable);
}
