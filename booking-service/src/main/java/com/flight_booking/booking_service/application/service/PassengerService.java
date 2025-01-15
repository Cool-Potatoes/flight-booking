package com.flight_booking.booking_service.application.service;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.domain.model.Passenger;
import com.flight_booking.booking_service.domain.repository.PassengerRepository;
import com.flight_booking.booking_service.presentation.global.exception.passenger.InvalidPassengerListException;
import com.flight_booking.booking_service.presentation.global.exception.passenger.MissingRequiredFieldsException;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.presentation.dto.PassengerResponseDto;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
  public List<PassengerResponseDto> createPassenger(
      List<PassengerRequestDto> passengerRequestDtos,
      Booking savedBooking
  ) {
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

    // PassengerResponseDto.of 메서드에 개별 필드를 전달하여 DTO 생성
    return savedPassengers.stream()
        .map(passenger -> PassengerResponseDto.from(
            passenger.getPassengerId(),
            passenger.getSeatId(),
            passenger.getPassengerType(),
            passenger.getPassengerName(),
            passenger.getBaggage(),
            passenger.getMeal()
        ))
        .collect(Collectors.toList());
  }

  public PassengerResponseDto getPassenger(UUID passengerId) {

    Passenger passenger = passengerRepository.findById(passengerId)
        .orElseThrow(
            () -> new NoSuchElementException("Passenger not found for ID: " + passengerId));

    return PassengerResponseDto.from(
        passenger.getPassengerId(),
        passenger.getSeatId(),
        passenger.getPassengerType(),
        passenger.getPassengerName(),
        passenger.getBaggage(),
        passenger.getMeal()
    );
  }

  public List<PassengerResponseDto> getPassengers(UUID bookingId) {

    // bookingId에 해당하는 모든 Passenger 객체를 가져옵니다
    List<Passenger> passengers = passengerRepository.findAllByBooking_BookingId(bookingId);

    // 각 Passenger 객체에서 필드들을 추출하여 PassengerResponseDto로 변환
    return passengers.stream()
        .map(passenger -> PassengerResponseDto.from(
            passenger.getPassengerId(),
            passenger.getSeatId(),
            passenger.getPassengerType(),
            passenger.getPassengerName(),
            passenger.getBaggage(),
            passenger.getMeal()
        ))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = false)
  public void updateOnePassenger(UUID passengerId) {

    Passenger passenger = passengerRepository.findById(passengerId).orElseThrow();

    // TODO : updatePassenger
    // deletedby 추가하거나 다른걸로 변경
    // 지금은 그냥 seatid만 null 로 변경 -> 제약조건 위반
    // passenger 이름 cancelled 로 바꿈
    passenger.updateOnePassenger();

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
