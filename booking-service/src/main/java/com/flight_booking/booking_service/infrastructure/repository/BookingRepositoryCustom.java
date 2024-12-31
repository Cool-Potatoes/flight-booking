package com.flight_booking.booking_service.infrastructure.repository;

import com.flight_booking.booking_service.presentation.response.BookingResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {

  Page<BookingResponse> findAll(Pageable pageable, Integer size);
  BookingResponse findByBookingId(UUID bookingId);
}
