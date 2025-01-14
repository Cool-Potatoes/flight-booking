package com.flight_booking.flight_service.infrastructure.messaging.kafkaEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckAndReturnRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityRefundRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SeatKafkaEndpoint {

  private final SeatService seatService;

  @KafkaListener(groupId = "seat-availability-check-and-update-group", topics = "seat-availability-check-and-update-topic")
  public void consumeSeatAvailabilityCheckAndUpdate(
      @Payload ApiResponse<SeatAvailabilityCheckRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityCheckRequestDto seatAvailabilityCheckRequestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityCheckRequestDto.class);

    seatService.consumeSeatAvailabilityCheckAndUpdate(seatAvailabilityCheckRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-check-and-return-group", topics = "seat-availability-check-and-return-topic")
  public void consumeSeatAvailabilityCheckAndReturn(
      @Payload ApiResponse<SeatAvailabilityCheckAndReturnRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityCheckAndReturnRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityCheckAndReturnRequestDto.class);

    seatService.seatAvailabilityCheckAndReturn(seatBookingRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-refund-group", topics = "seat-availability-refund-topic")
  public void consumeSeatAvailabilityRefund(
      @Payload ApiResponse<SeatAvailabilityRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityRefundRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityRefundRequestDto.class);

    seatService.refundSeatAvailability(seatBookingRequestDto);
  }
}
