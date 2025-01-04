package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.BookingStatusEnum;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.repository.BookingRepositoryImpl;
import com.flight_booking.booking_service.presentation.global.exception.booking.NotFoundBookingException;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
  private final PassengerService passengerService;


  @Transactional(readOnly = false)
  public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {

    Booking booking = Booking.builder()
        // TODO: 유저아이디는 로그인한 유저 아이디 가져올 것
        // .userId(loggedInUserId)
        .flightId(bookingRequestDto.flightId())
        .bookingStatus(BookingStatusEnum.BOOKING_WAITING) // 초기 예약 상태 설정
        .build();

    Booking savedBooking = bookingRepository.save(booking);

    List<Passenger> savedPassenger = passengerService.createPassenger(bookingRequestDto.passengerRequestDtos(), savedBooking);

    return BookingResponseDto.of(savedBooking, savedPassenger);
  }

  public PagedModel<BookingResponseCustomDto> getBookings(Predicate predicate, Pageable pageable) {

    return new PagedModel<>(bookingRepositoryImpl.findAllBookings(predicate, pageable));
  }

  public BookingResponseDto getBooking(UUID bookingId) {

    Booking booking = bookingRepository.findByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    return BookingResponseDto.from(booking);
  }

  @Transactional(readOnly = false)
  public BookingResponseDto updateBooking(UUID bookingId,
      BookingRequestDto bookingRequestDto) {

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBooking(bookingRequestDto.flightId(),
        bookingRequestDto.bookingStatus());

    passengerService.updatePassenger(booking, bookingRequestDto.passengerRequestDtos());

    bookingRepository.save(booking);

    return BookingResponseDto.from(booking);
  }

  @Transactional(readOnly = false)
  public void deleteBooking(UUID bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    booking.deleteBooking();
  }
}
