package com.flight_booking.flight_service.presentation.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatBookingRequestDto;
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

  @KafkaListener(groupId = "seat-availability-group", topics = "seat-availability-check-and-update-topic")
  public ApiResponse<?> consumeSeatBooking(@Payload ApiResponse<SeatBookingRequestDto> message) {

    ObjectMapper mapper = new ObjectMapper();
    SeatBookingRequestDto seatBookingRequestDto = mapper.convertValue(message.getData(),
        SeatBookingRequestDto.class);

    seatService.updateSeatAvailable(seatBookingRequestDto);

    return ApiResponse.ok("예매 결제 성공");
  }
}
