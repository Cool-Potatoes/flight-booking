package com.flight_booking.flight_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FlightKafkaEndpoint {

  private final FlightService flightService;

  @KafkaListener(groupId = "flight-cancel-availability", topics = "flight-cancel-availability")
  public void consumeFlightCancelAvailability(
      @Payload ApiResponse<FlightCancelRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    FlightCancelRequestDto flightCancelRequestDto = mapper.convertValue(message.getData(),
        FlightCancelRequestDto.class);

    flightService.checkAndCancelFlight(flightCancelRequestDto);
  }

}
