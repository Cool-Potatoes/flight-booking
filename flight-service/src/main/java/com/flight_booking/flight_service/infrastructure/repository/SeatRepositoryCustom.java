package com.flight_booking.flight_service.infrastructure.repository;

import com.flight_booking.flight_service.presentation.response.SeatResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatRepositoryCustom {

  Page<SeatResponseDto> findAll(UUID flightId, List<UUID> seatIdList, Predicate predicate,
      Pageable pageable);
}
