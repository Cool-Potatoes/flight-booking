package com.flight_booking.booking_service.presentation.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingSeatCheckRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaBookingEndpoint {

  private final BookingService bookingService;

  @KafkaListener(groupId = "payment-complete-group", topics = "payment-complete-topic")
  public void consumeProcessBooking(
      @Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.processBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "payment-fail-groups", topics = "payment-fail-topic")
  public void consumeProcessBookingfail(
      @Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.processBooking(bookingProcessRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-check-topic", topics = "seat-availability-check-success-topic")
  public void consumeSeatAvailabilityCheckSuccess(
      @Payload ApiResponse<BookingSeatCheckRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingSeatCheckRequestDto bookingSeatCheckRequestDto = mapper.convertValue(message.getData(),
        BookingSeatCheckRequestDto.class);

    bookingService.updateBookingFromKafka(bookingSeatCheckRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-check-topic", topics = "seat-availability-check-fail-topic")
  public void consumeSeatAvailabilityCheckFail(
      @Payload ApiResponse<BookingSeatCheckRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingSeatCheckRequestDto bookingSeatCheckRequestDto = mapper.convertValue(message.getData(),
        BookingSeatCheckRequestDto.class);

    bookingService.updateBookingFromKafka(bookingSeatCheckRequestDto);
  }
}
