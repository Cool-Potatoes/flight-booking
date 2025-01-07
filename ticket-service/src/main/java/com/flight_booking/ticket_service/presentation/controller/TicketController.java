package com.flight_booking.ticket_service.presentation.controller;

import com.flight_booking.ticket_service.application.service.TicketService;
import com.flight_booking.ticket_service.presentation.dto.TicketRequestDto;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.global.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tickets")
public class TicketController {

  private final TicketService ticketService;


  @PostMapping
  public ApiResponse<?> createTicket(@RequestBody TicketRequestDto ticketRequestDto) {

    TicketResponseDto ticketResponseDto = ticketService.createTicket(ticketRequestDto);

    return ApiResponse.ok(ticketResponseDto, "항공권 생성 성공");
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<?> getTicket(@PathVariable UUID ticketId) {

    TicketResponseDto ticketResponseDto = ticketService.getTicket(ticketId);

    return ApiResponse.ok(ticketResponseDto, "항공권 조회 성공");
  }

}
