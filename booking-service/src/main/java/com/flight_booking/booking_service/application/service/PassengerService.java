package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.repository.PassengerRepository;
import com.flight_booking.booking_service.presentation.global.exception.passenger.InvalidPassengerListException;
import com.flight_booking.booking_service.presentation.global.exception.passenger.MissingRequiredFieldsException;
import com.flight_booking.booking_service.presentation.response.PassengerResponseDto;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerService {

  private final PassengerRepository passengerRepository;

  @Transactional(readOnly = false)
  public List<PassengerResponseDto> createPassenger(List<PassengerRequestDto> passengerRequestDtos,
      Booking savedBooking) {

    // TODO : 좌석 이미 예약되어있는지 가져와서 체크
    //  승객 중복 예약? 가능? - 같은 승객이 자리 여러개

    if (passengerRequestDtos == null || passengerRequestDtos.isEmpty()) {
      throw new InvalidPassengerListException();
    }

    List<Passenger> passengers = passengerRequestDtos.stream()
        .map(passengerRequestDto -> {
          if (passengerRequestDto.seatId() == null || passengerRequestDto.passengerName() == null) {
            throw new MissingRequiredFieldsException();
          }
          return Passenger.builder()
              .seatId(passengerRequestDto.seatId())
              .passengerType(passengerRequestDto.passengerType())
              .passengerName(passengerRequestDto.passengerName())
              .baggage(passengerRequestDto.baggage())
              .meal(passengerRequestDto.meal())
              .booking(savedBooking)
              .build();
        })
        .collect(Collectors.toList());

    List<Passenger> savedPassengers = passengerRepository.saveAll(passengers);

    return savedPassengers.stream().map(PassengerResponseDto::of).collect(Collectors.toList());
  }

  @Transactional(readOnly = false)
  public void updatePassenger(Booking booking, PassengerRequestDto passengerRequestDtos) {

    List<Passenger> passengers = passengerRepository.findAllByBooking_BookingId(
        booking.getBookingId());

//    if (passengers.size() != bookingRequestDto.passengerDtos().size()) {
//      throw new PassengerListSizeMismatchException();
//    }

    for (int i = 0; i < passengers.size(); i++) {
      Passenger passenger = passengers.get(i);

      passenger.updatePassenger(
          booking,
          passengerRequestDtos.seatId(),
          passengerRequestDtos.passengerType(),
          passengerRequestDtos.passengerName(),
          passengerRequestDtos.baggage(),
          passengerRequestDtos.meal()
      );
    }
  }
}
