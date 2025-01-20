package com.flight_booking.booking_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.common.application.dto.BookingCreateRequestDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingRefundRequestDto;
import com.flight_booking.common.application.dto.BookingStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.PassengerIsdeletedUpdateTrueRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingKafkaEndpoint {

  private final BookingService bookingService;


  @KafkaListener(groupId = "create-booking-group", topics = "create-booking-topic")
  public void consumeCreateBooking(
      @Payload ApiResponse<BookingCreateRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingCreateRequestDto requestDto = mapper.convertValue(message.getData(),
        BookingCreateRequestDto.class);

    bookingService.rebookBooking(requestDto.bookingRequestDto(), requestDto.username());
  }

  @KafkaListener(groupId = "booking-complete-group", topics = "booking-complete-topic")
  public void consumeBookingComplete(@Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.processBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-fail-group", topics = "booking-fail-topic")
  public void consumeBookingFail(@Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.failBooking(bookingProcessRequestDto);
  }


  @KafkaListener(groupId = "booking-refund-fail-group", topics = "booking-refund-fail-topic")
  public void consumeBookingRefundFail(@Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.failRefundBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-refund-success-group", topics = "booking-refund-ticket-success-topic")
  public void consumeBookingRefundTicketComplete(
      @Payload ApiResponse<BookingRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingRefundRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingRefundRequestDto.class);

    bookingService.processRefundTicketBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-status-update-refund-group", topics = "booking-status-update-refund-topic")
  public void consumeBookingStatusUpdate(
      @Payload ApiResponse<BookingStatusUpdateRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingStatusUpdateRefundRequestDto requestDto = mapper.convertValue(message.getData(),
        BookingStatusUpdateRefundRequestDto.class);

    bookingService.updateBookingStatusRefund(requestDto);
  }

  @KafkaListener(groupId = "passenger-isdeleted-update-true-group", topics = "passenger-isdeleted-update-true-topic")
  public void consumePassengerIsdeletedUpdateTrue(
      @Payload ApiResponse<PassengerIsdeletedUpdateTrueRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    PassengerIsdeletedUpdateTrueRequestDto requestDto = mapper.convertValue(message.getData(),
        PassengerIsdeletedUpdateTrueRequestDto.class);

    bookingService.updatePassengerIsDeletedTrue(requestDto);
  }
}
