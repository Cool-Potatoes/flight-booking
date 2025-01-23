package com.flight_booking.ticket_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.ticket_service.application.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketKafkaEndpoint {

  private final TicketService ticketService;
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "ticket-creation-group", topics = "ticket-creation-topic")
  public void consumeCreateTicket(@Payload ApiResponse<TicketRequestDto> message) {

    TicketRequestDto ticketRequestDto = objectMapper.convertValue(
        message.getData(), TicketRequestDto.class
    );

    ticketService.createTicket(ticketRequestDto);
  }

  @KafkaListener(groupId = "ticket-cancel-unavailable-group", topics = "ticket-cancel-unavailable-topic")
  public void consumeCancelUnavailable(@Payload ApiResponse<FlightCancelRequestDto> message) {

    FlightCancelRequestDto flightCancelRequestDto = objectMapper.convertValue(
        message.getData(), FlightCancelRequestDto.class
    );

    ticketService.cancelFail(flightCancelRequestDto);
  }
}
