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
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingKafkaEndpoint {

  private final BookingService bookingService;
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "create-booking-group", topics = "create-booking-topic")
  public void consumeCreateBooking(
      @Payload ApiResponse<BookingCreateRequestDto> message) {

    BookingCreateRequestDto requestDto = objectMapper.convertValue(
        message.getData(), BookingCreateRequestDto.class
    );

    bookingService.rebookBooking(requestDto.bookingRequestDto(), requestDto.username());
  }

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "booking-complete-group", topics = "booking-complete-topic")
  public void consumeBookingComplete(@Payload ApiResponse<BookingProcessRequestDto> message) {

    BookingProcessRequestDto bookingProcessRequestDto = objectMapper.convertValue(
        message.getData(), BookingProcessRequestDto.class
    );

    bookingService.processBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-fail-group", topics = "booking-fail-topic")
  public void consumeBookingFail(@Payload ApiResponse<BookingProcessRequestDto> message) {

    BookingProcessRequestDto bookingProcessRequestDto = objectMapper.convertValue(
        message.getData(), BookingProcessRequestDto.class
    );

    bookingService.failBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-refund-fail-group", topics = "booking-refund-fail-topic")
  public void consumeBookingRefundFail(@Payload ApiResponse<BookingProcessRequestDto> message) {

    BookingProcessRequestDto bookingProcessRequestDto = objectMapper.convertValue(
        message.getData(), BookingProcessRequestDto.class
    );

    bookingService.failRefundBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-refund-success-group", topics = "booking-refund-ticket-success-topic")
  public void consumeBookingRefundTicketComplete(
      @Payload ApiResponse<BookingRefundRequestDto> message) {

    BookingRefundRequestDto bookingProcessRequestDto = objectMapper.convertValue(
        message.getData(), BookingRefundRequestDto.class
    );

    bookingService.processRefundTicketBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "booking-status-update-refund-group", topics = "booking-status-update-refund-topic")
  public void consumeBookingStatusUpdate(
      @Payload ApiResponse<BookingStatusUpdateRefundRequestDto> message) {

    BookingStatusUpdateRefundRequestDto requestDto = objectMapper.convertValue(
        message.getData(), BookingStatusUpdateRefundRequestDto.class
    );

    bookingService.updateBookingStatusRefund(requestDto);
  }

  @KafkaListener(groupId = "passenger-isdeleted-update-true-group", topics = "passenger-isdeleted-update-true-topic")
  public void consumePassengerIsdeletedUpdateTrue(
      @Payload ApiResponse<PassengerIsdeletedUpdateTrueRequestDto> message) {

    PassengerIsdeletedUpdateTrueRequestDto requestDto = objectMapper.convertValue(
        message.getData(), PassengerIsdeletedUpdateTrueRequestDto.class
    );

    bookingService.updatePassengerIsDeletedTrue(requestDto);
  }
}
