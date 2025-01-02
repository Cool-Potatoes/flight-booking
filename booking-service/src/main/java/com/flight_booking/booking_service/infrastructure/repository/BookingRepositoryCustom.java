package com.flight_booking.booking_service.infrastructure.repository;

import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {

  Page<BookingResponseCustomDto> findAll(Pageable pageable, Integer size);

  BookingResponseCustomDto findByBookingId(UUID bookingId);
}
