package com.flight_booking.booking_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.booking_service.domain.model.Booking;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseDto(
    UUID bookingId,
    String bookingStatus,
    UUID seatId,
    List<PassengerResponseDto> passengers
) {

  public static BookingResponseDto from(Booking booking) {
    List<PassengerResponseDto> passengerDtos = booking.getPassengers().stream()
        .map(passenger -> new PassengerResponseDto(
            passenger.getPassengerId(),
            passenger.getSeatId(),
            passenger.getPassengerType(),
            passenger.getPassengerName(),
            passenger.getBaggage(),
            passenger.getMeal()
            // TODO: 추가 예정
            // seatClass, seatNumber, price 등
        ))
        .collect(Collectors.toList());

    return new BookingResponseDto(
        booking.getBookingId(),
        booking.getBookingStatus().toString(),
        booking.getSeatId(),
        passengerDtos
    );
  }

  public static BookingResponseDto of(Booking savedBooking,
      List<PassengerResponseDto> passengerResponseDtoList) {
    List<PassengerResponseDto> passengerDtos = passengerResponseDtoList.stream()
        .map(passenger -> new PassengerResponseDto(
            passenger.passengerId(),
            passenger.seatId(),
            passenger.passengerType(),
            passenger.passengerName(),
            passenger.baggage(),
            passenger.meal()
            // TODO: 추가 예정
            // seatClass, seatNumber, price 등
        ))
        .collect(Collectors.toList());

    return new BookingResponseDto(
        savedBooking.getBookingId(),
        savedBooking.getBookingStatus().toString(),
        savedBooking.getSeatId(),
        passengerDtos
    );
  }
}
