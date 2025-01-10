package com.flight_booking.booking_service.presentation.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.booking_service.application.service.BookingService;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
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
  public ApiResponse<?> consumeProcessBooking(
      @Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.processBooking(bookingProcessRequestDto);

    return ApiResponse.ok("예매 결제 성공");
  }

  @KafkaListener(groupId = "payment-complete-groups", topics = "payment-complete-topic-fail")
  public ApiResponse<?> consumeProcessBookingfail(
      @Payload ApiResponse<BookingProcessRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    BookingProcessRequestDto bookingProcessRequestDto = mapper.convertValue(message.getData(),
        BookingProcessRequestDto.class);

    bookingService.processBooking(bookingProcessRequestDto);

    return ApiResponse.ok("예매 결제 성공");
  }
}
