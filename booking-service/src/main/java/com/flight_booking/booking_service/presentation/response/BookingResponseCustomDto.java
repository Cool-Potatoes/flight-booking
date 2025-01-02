package com.flight_booking.booking_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.booking_service.domain.model.Booking;
import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseCustomDto(
    UUID bookingId,
    Long userId,
    UUID flightId
) {

  @QueryProjection
  public BookingResponseCustomDto(Booking booking){
    this(booking.getBookingId(), booking.getUserId(), booking.getFlightId());
  }
}