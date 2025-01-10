package com.flight_booking.flight_service.presentation.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatBookingRequestDto;
import com.flight_booking.common.application.dto.SeatCheckingRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaSeatEndpoint {

  private final SeatService seatService;

  @KafkaListener(groupId = "seat-availability-update-group", topics = "seat-availability-check-and-update-topic")
  public void consumeSeatAvailabilityCheckAndUpdate(
      @Payload ApiResponse<SeatBookingRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatBookingRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatBookingRequestDto.class);

    seatService.consumeSeatAvailabilityCheckAndUpdate(seatBookingRequestDto);
  }

  @KafkaListener(groupId = "seat-availability-check-group", topics = "seat-availability-check-topic")
  public void consumeSeatAvailabilityCheck(@Payload ApiResponse<SeatCheckingRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatCheckingRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatCheckingRequestDto.class);

    seatService.checkSeatAvailable(seatBookingRequestDto);
  }
}
