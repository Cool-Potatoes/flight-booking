package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.repository.BookingRepositoryImpl;
import com.flight_booking.booking_service.presentation.global.exception.booking.NotFoundBookingException;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
import com.flight_booking.booking_service.presentation.request.BookingUpdateRequestDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseDto;
import com.flight_booking.booking_service.presentation.response.PassengerResponseDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingSeatCheckRequestDto;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.application.dto.SeatBookingRequestDto;
import com.flight_booking.common.application.dto.SeatCheckingRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingRepositoryImpl bookingRepositoryImpl;
  private final PassengerService passengerService;
  private final KafkaTemplate<String, ApiResponse<?>> kafkaTemplate;

  @Transactional(readOnly = false)
  public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {

    Booking booking = Booking.builder()
        // TODO: 유저아이디는 로그인한 유저 아이디 가져올 것
        // .userId(loggedInUserId)
        .bookingStatus(BookingStatusEnum.BOOKING_WAITING)
        .build();

    // 임시 이메일, 로그인한 유저한테 가져올것 - 게이트웨이 헤더
    String email = "test@test.com";

    Booking savedBooking = bookingRepository.save(booking);

    List<PassengerResponseDto> passengerResponseDtoList = passengerService.createPassenger(
        bookingRequestDto.passengerRequestDtos(), savedBooking);

    passengerResponseDtoList.forEach(passenger -> {
      kafkaTemplate.send(
          "seat-availability-check-and-update-topic",
          savedBooking.getBookingId().toString(),
          ApiResponse.ok(
              new SeatBookingRequestDto(email, savedBooking.getBookingId(), passenger.seatId()),
              "Message from createBooking"
          )
      );
    });

    return BookingResponseDto.of(savedBooking, passengerResponseDtoList);
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
      BookingUpdateRequestDto bookingRequestDto) {

    for (PassengerRequestDto passengerRequestDto : bookingRequestDto.passengerRequestDtos()) {
      kafkaTemplate.send(
          "seat-availability-check-topic",
          passengerRequestDto.seatId().toString(),
          ApiResponse.ok(
              new SeatCheckingRequestDto(passengerRequestDto.seatId(), bookingId,
                  passengerRequestDto),
              "Message from updateBooking"
          )
      );
    }

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    return BookingResponseDto.from(booking);
  }

  // TODO : 예약 취소 메서드
  @Transactional(readOnly = false)
  public void deleteBooking(UUID bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    booking.deleteBooking();
  }

  @Transactional(readOnly = false)
  public BookingResponseDto processBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(bookingProcessRequestDto.bookingStatus());

    return BookingResponseDto.from(booking);
  }

  @Transactional(readOnly = false)
  public void updateBookingFromKafka(BookingSeatCheckRequestDto bookingSeatCheckRequestDto) {

    Booking booking = bookingRepository.findById(bookingSeatCheckRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    if (bookingSeatCheckRequestDto.check()) {

      booking.updateBooking(BookingStatusEnum.BOOKING_COMPLETE);

      passengerService.updatePassenger(booking, bookingSeatCheckRequestDto.passengerRequestDto());

      bookingRepository.save(booking);

    } else {
      booking.updateBooking(BookingStatusEnum.BOOKING_FAIL);
    }
  }
}
