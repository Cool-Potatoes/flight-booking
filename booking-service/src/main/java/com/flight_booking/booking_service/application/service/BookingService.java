package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.presentation.request.BookingRequest;
import com.flight_booking.booking_service.presentation.response.BookingResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;

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
}
