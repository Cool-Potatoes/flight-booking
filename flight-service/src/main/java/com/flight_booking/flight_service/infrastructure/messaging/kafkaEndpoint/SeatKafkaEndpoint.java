package com.flight_booking.flight_service.infrastructure.messaging.kafkaEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckAndReturnRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckForRebookRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityRefundRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
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

  @KafkaListener(groupId = "seat-availability-check-and-update-for-rebook-group", topics = "seat-availability-check-and-update-for-rebook-topic")
  public void consumeSeatAvailabilityCheckAndUpdateForRebook(
      @Payload ApiResponse<SeatAvailabilityCheckForRebookRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityCheckForRebookRequestDto requestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityCheckForRebookRequestDto.class);

    seatService.consumeSeatAvailabilityCheckAndUpdateForRebook(requestDto);
  }

  @KafkaListener(groupId = "seat-calculate-difference-and-refund-group", topics = "seat-calculate-difference-and-refund-topic")
  public void consumeSeatCalculateDifferenceAndRefund(
      @Payload ApiResponse<SeatCalculateDifferenceAndRefundRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatCalculateDifferenceAndRefundRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatCalculateDifferenceAndRefundRequestDto.class);

    seatService.seatCalculateDifferenceAndRefund(seatBookingRequestDto);
  }


  @KafkaListener(groupId = "seat-availability-refund-group", topics = "seat-availability-refund-topic")
  public void consumeSeatAvailabilityRefund(
      @Payload ApiResponse<SeatAvailabilityUpdateTrueRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityUpdateTrueRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityUpdateTrueRequestDto.class);

    seatService.seatAvailabilityUpdateTrue(seatBookingRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-update-true-group", topics = "seat-availability-update-true-topic")
  public void consumeSeatAvailabilityUpdateTrue(
      @Payload ApiResponse<SeatAvailabilityUpdateTrueRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatAvailabilityUpdateTrueRequestDto requestDto = mapper.convertValue(message.getData(),
        SeatAvailabilityUpdateTrueRequestDto.class);

    seatService.seatAvailabilityUpdateTrue(requestDto);
  }
}
