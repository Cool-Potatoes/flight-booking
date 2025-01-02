package com.flight_booking.booking_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.infrastructure.client.PassengerTypeEnum;
import com.flight_booking.booking_service.infrastructure.client.SeatClassEnum;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseDto(
    UUID passengerId,
    String passengerName,
    PassengerTypeEnum passengerType,
    Boolean baggage,
    Boolean meal,
    SeatClassEnum seatClass,
    String seatNumber,
    Long price
) {
  public static List<BookingResponseDto> from(Booking booking) {
    return booking.getPassengers().stream()
        .map(passenger -> new BookingResponseDto(
            passenger.getPassengerId(),
            passenger.getPassengerName(),
            passenger.getPassengerType(),
            passenger.getBaggage(),
            passenger.getMeal(),
            // TODO: 추가 예정
            null, // seatClass
            null, // seatNumber
            null  // price
        ))
        .collect(Collectors.toList());
  }
}
