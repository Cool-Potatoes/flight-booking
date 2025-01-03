package com.flight_booking.booking_service.infrastructure.repository;

import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {

  Page<BookingResponseCustomDto> findAllBookings(Predicate predicate, Pageable pageable);
}
