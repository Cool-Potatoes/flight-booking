package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.repository.BookingRepository;
import com.flight_booking.booking_service.infrastructure.messaging.BookingKafkaSender;
import com.flight_booking.booking_service.presentation.global.exception.booking.NotFoundBookingException;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.BookingUpdateRequestDto;
import com.flight_booking.common.application.dto.PassengerIsdeletedUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.ReBookingRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckForRebookRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.common.presentation.dto.BookingRequestDto;
import com.flight_booking.common.presentation.dto.BookingResponseDto;
import com.flight_booking.common.presentation.dto.PassengerResponseDto;
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
      String email) {

    Booking booking = Booking.builder()
        .email(email)
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
            email, savedBooking.getBookingId(), seatIdList),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );

    return BookingResponseDto.from(savedBooking.getBookingId(),
        savedBooking.getBookingStatus().toString(), passengerResponseDtoList);
  }


  @Transactional(readOnly = false)
  public void rebookBooking(ReBookingRequestDto bookingRequestDto,
      String email) {

    Booking booking = Booking.builder()
        .email(email)
        .bookingStatus(BookingStatusEnum.BOOKING_CREATE)
        .build();

    Booking savedBooking = bookingRepository.save(booking);

    passengerService.createPassengerForRebook(bookingRequestDto.passengerRequestDto(),
        savedBooking);

    bookingKafkaSender.sendMessage(
        "seat-availability-check-and-update-for-rebook-topic",
        savedBooking.getBookingId().toString(),
        new SeatAvailabilityCheckForRebookRequestDto(
            email, bookingRequestDto.ticketId(), savedBooking.getBookingId(),
            bookingRequestDto.passengerRequestDto().seatId()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );
  }

  public PagedModel<BookingResponseCustomDto> getBookings(Predicate predicate, Pageable pageable) {

    return new PagedModel<>(bookingRepository.findAllBookings(predicate, pageable));
  }

  public BookingResponseDto getBooking(UUID bookingId) {

    Booking booking = bookingRepository.findByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(NotFoundBookingException::new);

    List<PassengerResponseDto> passengerResponseDtoList = passengerService.getPassengers(
        booking.getBookingId());

    return BookingResponseDto.from(bookingId, booking.getBookingStatus().toString(),
        passengerResponseDtoList);
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

    List<PassengerResponseDto> passengerResponseDtoList = passengerService.getPassengers(
        booking.getBookingId());

    return BookingResponseDto.from(bookingId, booking.getBookingStatus().toString(),
        passengerResponseDtoList);
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
          new TicketRequestDto(bookingProcessRequestDto.ticketId(),
              bookingProcessRequestDto.email(), booking.getBookingId(), passenger.getPassengerId(),
              passenger.getSeatId()),
          StackTraceUtils.getCurrentMethodName(),
          StackTraceUtils.getCurrentClassName()
      );

    }
  }

  @Transactional
  public void failBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_FAIL);

    List<UUID> seatIdList = booking.getPassengers().stream().map(Passenger::getSeatId).toList();
    for (UUID seatId : seatIdList) {
      bookingKafkaSender.sendMessage("seat-availability-update-true-topic",
          booking.getBookingId().toString(),
          new SeatAvailabilityUpdateTrueRequestDto(seatId, true),
          StackTraceUtils.getCurrentMethodName(),
          StackTraceUtils.getCurrentClassName());
    }
  }


  @Transactional
  public void failRefundBooking(BookingProcessRequestDto bookingProcessRequestDto) {

    Booking booking = bookingRepository.findById(bookingProcessRequestDto.bookingId())
        .orElseThrow(NotFoundBookingException::new);

    booking.updateBookingStatus(BookingStatusEnum.BOOKING_REFUND_FAIL);
  }

  @Transactional(readOnly = false)
  public void updateBookingStatusRefund(BookingStatusUpdateRefundRequestDto requestDto) {
    Booking booking = getBookingEntity(requestDto.bookingId());

    booking.updateBookingStatus(requestDto.bookingStatusEnum());
  }

  private Booking getBookingEntity(UUID bookingId) {

    return bookingRepository.findByBookingIdAndIsDeletedFalse(bookingId)
        .orElseThrow(NotFoundBookingException::new);
  }

  @Transactional(readOnly = false)
  public void updatePassengerIsDeletedTrue(PassengerIsdeletedUpdateTrueRequestDto requestDto) {

    passengerService.updatePassengerIsDeletedTrue(requestDto);
  }
}
