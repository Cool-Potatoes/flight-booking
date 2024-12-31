package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.repository.BookingRepositoryImpl;
import com.flight_booking.booking_service.presentation.request.BookingRequest;
import com.flight_booking.booking_service.presentation.response.BookingResponse;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustom;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingRepositoryImpl bookingRepositoryImpl;

  @Transactional
  public List<BookingResponse> createBooking(BookingRequest bookingRequest) {

    Booking booking = Booking.builder()
// TODO : 유저아이디는 로그인한 유저 아이디 가져올것
//        .userId()
        .flightId(bookingRequest.getFlightId())
        .passengers(bookingRequest.getPassengers())
//        .passengerId(bookingRequest.getPassengerId())
        .build();

    bookingRepository.save(booking);

    return BookingResponse.from(booking);
  }

  public Page<BookingResponseCustom> getBookings(Pageable pageable, Integer size) {

    return bookingRepositoryImpl.findAll(pageable, size);
  }

  public BookingResponse getBooking(UUID bookingId) {

    return bookingRepositoryImpl.findByBookingId(bookingId);
  }
}
