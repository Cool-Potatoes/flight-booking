package com.flight_booking.booking_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.model.PassengerTypeEnum;
import com.flight_booking.booking_service.infrastructure.client.SeatClassEnum;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseDto(
    UUID bookingId,           // 예약 ID 추가
    String bookingStatus,     // 예약 상태 추가
    UUID flightId,          // 비행기 ID 추가
    List<PassengerResponseDto> passengers // 승객 정보 리스트 추가
) {
  // BookingResponseDto는 이제 예약 정보와 승객 리스트를 포함합니다.

  // Booking과 승객 리스트를 포함한 DTO 반환
  public static BookingResponseDto from(Booking booking) {
    List<PassengerResponseDto> passengerDtos = booking.getPassengers().stream()
        .map(passenger -> new PassengerResponseDto(
            passenger.getPassengerId(),
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
        booking.getBookingStatus().toString(), // 예시로 BookingStatus를 String으로 변환
        booking.getFlightId(), // 비행기 ID
        passengerDtos
    );
  }

  // 예약과 승객 리스트를 포함한 DTO 반환
  public static BookingResponseDto of(Booking savedBooking, List<Passenger> passengers) {
    // 승객 리스트를 PassengerResponseDto로 변환
    List<PassengerResponseDto> passengerDtos = passengers.stream()
        .map(passenger -> new PassengerResponseDto(
            passenger.getPassengerId(),
            passenger.getPassengerType(),
            passenger.getPassengerName(),
            passenger.getBaggage(),
            passenger.getMeal()
            // TODO: 추가 예정
            // seatClass, seatNumber, price 등
        ))
        .collect(Collectors.toList());

    // 예약 정보를 포함한 DTO 생성
    return new BookingResponseDto(
        savedBooking.getBookingId(),
        savedBooking.getBookingStatus().toString(), // BookingStatus를 String으로 변환
        savedBooking.getFlightId(),
        passengerDtos
    );
  }
}
