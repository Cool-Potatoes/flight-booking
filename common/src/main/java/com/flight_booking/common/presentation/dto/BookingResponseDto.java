package com.flight_booking.common.presentation.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseDto(
    UUID bookingId,
    String bookingStatus,
    List<PassengerResponseDto> passengerResponseDtoList
) {

  public static BookingResponseDto from(
      UUID bookingId,
      String bookingStatus,
      List<PassengerResponseDto> passengerResponseDtoList
  ) {
    return new BookingResponseDto(bookingId, bookingStatus, passengerResponseDtoList);
  }
}