package com.flight_booking.flight_service.infrastructure.messaging.kafkaEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckForRebookRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SeatKafkaEndpoint {

  private final SeatService seatService;
  private final ObjectMapper objectMapper;

  @KafkaListener(groupId = "seat-availability-check-and-update-group", topics = "seat-availability-check-and-update-topic")
  public void consumeSeatAvailabilityCheckAndUpdate(
      @Payload ApiResponse<SeatAvailabilityCheckRequestDto> message) {

    SeatAvailabilityCheckRequestDto seatAvailabilityCheckRequestDto = objectMapper.convertValue(
        message.getData(), SeatAvailabilityCheckRequestDto.class
    );

    seatService.consumeSeatAvailabilityCheckAndUpdate(seatAvailabilityCheckRequestDto);
  }

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "seat-availability-check-and-update-for-rebook-group", topics = "seat-availability-check-and-update-for-rebook-topic")
  public void consumeSeatAvailabilityCheckAndUpdateForRebook(
      @Payload ApiResponse<SeatAvailabilityCheckForRebookRequestDto> message) {

    SeatAvailabilityCheckForRebookRequestDto requestDto = objectMapper.convertValue(
        message.getData(), SeatAvailabilityCheckForRebookRequestDto.class
    );

    seatService.consumeSeatAvailabilityCheckAndUpdateForRebook(requestDto);
  }

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(groupId = "seat-calculate-difference-and-refund-group", topics = "seat-calculate-difference-and-refund-topic")
  public void consumeSeatCalculateDifferenceAndRefund(
      @Payload ApiResponse<SeatCalculateDifferenceAndRefundRequestDto> message) {

    SeatCalculateDifferenceAndRefundRequestDto seatBookingRequestDto = objectMapper.convertValue(
        message.getData(), SeatCalculateDifferenceAndRefundRequestDto.class
    );
    seatService.seatCalculateDifferenceAndRefund(seatBookingRequestDto);

  }

  @KafkaListener(groupId = "seat-availability-update-true-group", topics = "seat-availability-update-true-topic")
  public void consumeSeatAvailabilityUpdateTrue(
      @Payload ApiResponse<SeatAvailabilityUpdateTrueRequestDto> message) {

    SeatAvailabilityUpdateTrueRequestDto requestDto = objectMapper.convertValue(
        message.getData(), SeatAvailabilityUpdateTrueRequestDto.class
    );

    seatService.seatAvailabilityUpdateTrue(requestDto);
  }
}
