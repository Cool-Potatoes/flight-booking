package com.flight_booking.ticket_service.presentation.controller;

import com.flight_booking.common.application.dto.TicketRequestDto;
import com.flight_booking.common.infrastructure.security.CustomUserDetails;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.ticket_service.application.service.TicketService;
import com.flight_booking.ticket_service.domain.model.Ticket;
import com.flight_booking.ticket_service.presentation.dto.TicketResponseDto;
import com.flight_booking.ticket_service.presentation.dto.TicketUpdateRequestDto;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tickets")
public class TicketController {

  private final TicketService ticketService;


  @PostMapping
  public ApiResponse<?> createTicket(@Valid @RequestBody TicketRequestDto ticketRequestDto) {

    TicketResponseDto ticketResponseDto = ticketService.createTicket(ticketRequestDto);

    return ApiResponse.ok(ticketResponseDto, "항공권 생성 성공");
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<?> getTicket(@PathVariable UUID ticketId) {

    // TODO 사용자 추가 본인 것만 조회

    TicketResponseDto ticketResponseDto = ticketService.getTicket(ticketId);

    return ApiResponse.ok(ticketResponseDto, "항공권 조회 성공");
  }

  /**
   * 본인의 항공권 정보 목록 조회
   *
   * @param uuidList  항공권 id를 list로 입력하여 조회
   * @param predicate 검색어로 조회
   * @param pageable  정렬
   * @return TicketResponseDto의 PagedModel
   */
  @GetMapping
  public ApiResponse<?> getTicketsPage(
      @RequestParam(required = false) List<UUID> uuidList,
      @QuerydslPredicate(root = Ticket.class) Predicate predicate,
      Pageable pageable
  ) {

    String email = "tmpUser"; // TODO 사용자 추가

    PagedModel<TicketResponseDto> flightResponseDtoPagedModel
        = ticketService.getTicketsPage(email, uuidList, predicate, pageable);

    return ApiResponse.ok(flightResponseDtoPagedModel, "항공권 목록 조회 성공");
  }

  @PutMapping("/{ticketId}")
  public ApiResponse<?> updateTicket(
      @PathVariable UUID ticketId,
      @Valid @RequestBody TicketUpdateRequestDto ticketRequestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    // TODO 본인 것인지 권한 확인

    TicketResponseDto ticketResponseDto = ticketService.updateTicket(ticketId, ticketRequestDto, userDetails);

    return ApiResponse.ok(ticketResponseDto, "항공권 수정 대기중");
  }

  @PatchMapping("/{ticketId}")
  public ApiResponse<?> cancelTicket(@PathVariable UUID ticketId, @AuthenticationPrincipal CustomUserDetails userDetails) {

    // TODO 권한 확인

    ticketService.cancelTicket(ticketId, userDetails.getUsername());

    return ApiResponse.ok("항공권 취소 요청됨");
  }

}
