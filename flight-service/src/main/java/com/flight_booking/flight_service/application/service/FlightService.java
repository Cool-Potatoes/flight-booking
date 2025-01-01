package com.flight_booking.flight_service.application.service;

import com.flight_booking.flight_service.domain.model.Airport;
import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.domain.repository.AirportRepository;
import com.flight_booking.flight_service.domain.repository.FlightRepository;
import com.flight_booking.flight_service.domain.repository.SeatRepository;
import com.flight_booking.flight_service.presentation.request.FlightCreateRequestDto;
import com.flight_booking.flight_service.presentation.request.FlightUpdateRequestDto;
import com.flight_booking.flight_service.presentation.response.FlightDetailResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlightService {

  private final AirportRepository airportRepository;
  private final SeatRepository seatRepository;
  private final FlightRepository flightRepository;

  public FlightDetailResponseDto getFlightById(UUID flightId) {
    Flight flight = flightRepository.findByFlightIdAndIsDeletedFalse(flightId).orElseThrow(
        //TODO: Error 타입 정해지면 수정
        () -> new RuntimeException("해당하는 항공편이 존재하지 않습니다.")
    );

    return FlightDetailResponseDto.from(flight);
  }

  @Transactional
  public UUID createFlight(FlightCreateRequestDto requestDto) {

    Airport departureAirport = airportRepository.findByCityName(requestDto.departureAirport())
        .orElseThrow(
            //TODO: Error 타입 정해지면 수정
            () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
        );
    Airport arrivalAirport = airportRepository.findByCityName(requestDto.arrivalAirport())
        .orElseThrow(
            () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
        );

    Flight flight = flightRepository.save(
        Flight.create(requestDto, departureAirport, arrivalAirport)
    );

    //TODO : seat create 추가

    return flight.getFlightId();
  }

  @Transactional
  public UUID updateFlight(UUID flightId, FlightUpdateRequestDto requestDto) {
    Flight flight = flightRepository.findByFlightIdAndIsDeletedFalse(flightId).orElseThrow(
        () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
    );

    flight.update(requestDto);

    return flight.getFlightId();
  }

  public UUID deleteFlight(UUID flightId) {

    Flight flight = flightRepository.findByFlightIdAndIsDeletedFalse(flightId).orElseThrow(
        () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
    );

    //TODO: 유저 구현 후 수정
    String deletedBy = "tmpUser";

    flight.delete(deletedBy);

    return flight.getFlightId();
  }
}
