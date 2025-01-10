package com.flight_booking.ticket_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.ticket_service.application.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketKafkaEndpoint {

  private final TicketService ticketService;

  @KafkaListener(groupId = "ticket-creation-group", topics = "ticket-creation-topic")
  public void consumeCreateTicket(@Payload ApiResponse<TicketRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    TicketRequestDto ticketRequestDto = mapper.convertValue(message.getData(),
        TicketRequestDto.class);

    ticketService.createTicket(ticketRequestDto);
  }

}
