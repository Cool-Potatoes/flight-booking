package com.flight_booking.flight_service.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatBookingRequestDto;
import com.flight_booking.flight_service.application.service.SeatService;
import com.flight_booking.flight_service.domain.model.Seat;
import com.flight_booking.flight_service.presentation.global.ApiResponse;
import com.flight_booking.flight_service.presentation.request.SeatRequestDto;
import com.flight_booking.flight_service.presentation.response.SeatResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flights/{flightId}/seats")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;

  @PatchMapping("/price")
  public ApiResponse<?> updateFlightWholeSeatPrice(
      @PathVariable UUID flightId,
      @RequestBody SeatRequestDto seatRequestDto
  ) {
    seatService.updateFlightWholeSeatPrice(flightId, seatRequestDto);
    return ApiResponse.ok("좌석 가격 수정 성공");
  }

  @GetMapping
  public ApiResponse<?> getSeatsByFlightId(
      @PathVariable UUID flightId,
      @RequestParam(required = false) List<UUID> seatIdList,
      @QuerydslPredicate(root = Seat.class) Predicate predicate,
      Pageable pageable
  ) {
    PagedModel<SeatResponseDto> seatResponseDtoPagedModel
        = seatService.getSeatsPage(flightId, seatIdList, predicate, pageable);

    return ApiResponse.ok(seatResponseDtoPagedModel, "좌석 데이터 목록 조회 성공");
  }

  @KafkaListener(groupId = "seat-booking-group", topics = "seat-booking-topic")
  public ApiResponse<?> consumeSeatBooking(@Payload ApiResponse<SeatBookingRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatBookingRequestDto flightRequestDto = mapper.convertValue(message.getData(),
        SeatBookingRequestDto.class);

//    seatService.processBooking(flightRequestDto);

    return ApiResponse.ok("예매 결제 성공");
  }
}
