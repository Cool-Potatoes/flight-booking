package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.messaging.BookingKafkaSender;
import com.flight_booking.booking_service.presentation.global.exception.booking.NotFoundBookingException;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.BookingResponseDto;
import com.flight_booking.booking_service.presentation.response.PassengerResponseDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingRefundRequestDto;
import com.flight_booking.common.application.dto.BookingUpdateRequestDto;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckAndReturnRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityRefundRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.application.dto.TicketUpdateStatusRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.querydsl.core.types.Predicate;
import java.util.ArrayList;
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
  private final PassengerService passengerService;
  private final BookingKafkaSender bookingKafkaSender;

  @Transactional(readOnly = false)
  public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto,
      String username) {

    Booking booking = Booking.builder()
        .email(username)
        .bookingStatus(BookingStatusEnum.BOOKING_WAITING)
        .build();

    Booking savedBooking = bookingRepository.save(booking);

    List<PassengerResponseDto> passengerResponseDtoList = passengerService.createPassenger(
        bookingRequestDto.passengerRequestDtos(), savedBooking);

    List<UUID> seatIdList = new ArrayList<>();
    for (PassengerResponseDto passengerResponseDto : passengerResponseDtoList) {
      seatIdList.add(passengerResponseDto.seatId());
    }

    bookingKafkaSender.sendMessage(
        "seat-availability-check-and-update-topic",
        savedBooking.getBookingId().toString(),
        new SeatAvailabilityCheckRequestDto(
            username, savedBooking.getBookingId(), seatIdList)
    );

    return BookingResponseDto.of(savedBooking, passengerResponseDtoList);
  }

  public PagedModel<BookingResponseCustomDto> getBookings(Predicate predicate, Pageable pageable) {

    return new PagedModel<>(bookingRepository.findAllBookings(predicate, pageable));
  }

  public BookingResponseDto getBooking(UUID bookingId) {

    Booking booking = bookingRepository.findByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    return BookingResponseDto.from(booking);
  }

  // 미 사용 메서드
  @Transactional(readOnly = false)
  public BookingResponseDto updateBooking(UUID bookingId,
      BookingUpdateRequestDto bookingRequestDto) {

    Booking booking = bookingRepository.findByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    // TODO : 임시
    String email = "test@test.com";

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_CHANGE_PENDING);

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
  public void processBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_COMPLETE);

    // 항공권 생성
    for (Passenger passenger : booking.getPassengers()) {

      bookingKafkaSender.sendMessage(
          "ticket-creation-topic",
          booking.getBookingId().toString(),
          new TicketRequestDto(
              booking.getBookingId(), passenger.getPassengerId(), passenger.getSeatId())
      );

    }
  }

  @Transactional
  public void failBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_FAIL);
  }

  @Transactional(readOnly = false)
  public void processRefundBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    List<PassengerRequestDto> passengerRequestDtos = bookingProcessRequestDto.passengerRequestDtos();

    BookingRequestDto bookingRequestDto = new BookingRequestDto(passengerRequestDtos);

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_CHANGE_PENDING);

    createBooking(bookingRequestDto, bookingProcessRequestDto.email());

    bookingKafkaSender.sendMessage(
        "ticket-update-topic",
        booking.getBookingId().toString(),
        new TicketUpdateStatusRequestDto(
            bookingProcessRequestDto.ticketId())
    );

  }

  @Transactional
  public void failRefundBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_REFUND_FAIL);
  }

  @Transactional(readOnly = false)
  public void processRefundTicketBooking(BookingRefundRequestDto bookingProcessRequestDto) {

    // TODO
    passengerService.updateOnePassenger(bookingProcessRequestDto.passengerId());

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_CANCELLED);

    // TODO : 물어볼거 1 = 토픽에 들어가는 id는 해당 도메인 기준?
    bookingKafkaSender.sendMessage(
        "seat-availability-refund-topic",
        booking.getBookingId().toString(),
        new SeatAvailabilityRefundRequestDto(
            bookingProcessRequestDto.seatId())
    );
  }

  @Transactional(readOnly = false)
  public void updateBookingStatus(BookingUpdateRequestDto bookingRequestDto) {

    Booking booking = bookingRepository.findByBookingIdAndIsDeletedFalse(
            bookingRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    String userEmail = bookingRequestDto.email();

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_CHANGE_PENDING);

    bookingKafkaSender.sendMessage(
        "seat-availability-check-and-return-topic",
        booking.getBookingId().toString(),
        new SeatAvailabilityCheckAndReturnRequestDto(bookingRequestDto.ticketId(),
            userEmail, booking.getBookingId(), bookingRequestDto.passengerRequestDtos())
    );
  }
}
