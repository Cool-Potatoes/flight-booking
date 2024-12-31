package com.flight_booking.booking_service.presentation.response;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.infrastructure.client.Passenger;
import com.flight_booking.booking_service.infrastructure.client.PassengerTypeEnum;
import com.flight_booking.booking_service.infrastructure.client.SeatClassEnum;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

  private UUID passengerId;
  private String passengerName;
  private PassengerTypeEnum passengerType;
  private Boolean baggage;
  private Boolean meal;
  private SeatClassEnum seatClass;
  private String seatNumber;
  private Long price;

//  public static List<BookingResponse> from(Booking booking) {
//    return BookingResponse.builder()
//        .passengerId(booking.getPassengers().)
//        .build();
//
//  }

  public static List<BookingResponse> from(Booking booking) {
    return booking.getPassengers().stream()
        .map(passenger -> BookingResponse.builder()
            .passengerId(passenger.getPassengerId())
            .passengerName(passenger.getPassengerName())
            .passengerType(passenger.getPassengerType())
            .baggage(passenger.getBaggage())
            .meal(passenger.getMeal())
// TODO : 추가예정
//            .seatClass(passenger.getSeatClass())
//            .seatNumber(passenger.getSeatNumber())
//            .price(passenger.getPrice())
            .build())
        .collect(Collectors.toList());
  }
}
