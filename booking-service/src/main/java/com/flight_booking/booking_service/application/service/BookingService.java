package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.repository.BookingRepositoryImpl;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingRepositoryImpl bookingRepositoryImpl;

  @Transactional(readOnly = false)
  public List<BookingResponseDto> createBooking(BookingRequestDto bookingRequestDto) {

    Booking booking = Booking.builder()
// TODO : 유저아이디는 로그인한 유저 아이디 가져올것
//        .userId()
        .flightId(bookingRequestDto.flightId())
        .passengers(bookingRequestDto.passengers())
//        .passengerId(bookingRequest.getPassengerId())
        .build();

    bookingRepository.save(booking);

    return BookingResponseDto.from(booking);
  }

  public PagedModel<BookingResponseCustomDto> getBookings(Pageable pageable, Integer size) {

    return new PagedModel<>(bookingRepositoryImpl.findAll(pageable,size));
  }

  public BookingResponseCustomDto getBooking(UUID bookingId) {

    return bookingRepositoryImpl.findByBookingId(bookingId);
  }

  @Transactional(readOnly = false)
  public List<BookingResponseDto> updateBooking(UUID bookingId, BookingRequestDto bookingRequestDto) {

    Booking booking = bookingRepository.findById(bookingId).orElseThrow();

    booking.updateBooking(bookingRequestDto);
    bookingRepository.save(booking);

    return BookingResponseDto.from(booking);
  }

  @Transactional(readOnly = false)
  public void deleteBooking(UUID bookingId) {

    Booking booking = bookingRepository.findById(bookingId).orElseThrow();
    booking.deleteBooking();
  }
}
