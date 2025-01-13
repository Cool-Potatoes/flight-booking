package com.flight_booking.booking_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.booking_service.domain.model.Booking;
import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseCustomDto(
    UUID bookingId,
    String email
) {

  @QueryProjection
  public BookingResponseCustomDto(Booking booking){
    this(booking.getBookingId(), booking.getEmail());
  }
}