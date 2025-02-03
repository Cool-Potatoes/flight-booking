package com.flight_booking.flight_service.application.service;

import com.flight_booking.common.application.dto.FlightCancelRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundFromTicketRequestDto;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.flight_service.domain.model.Airport;
import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.domain.model.FlightStatusEnum;
import com.flight_booking.flight_service.domain.repository.FlightRepository;
import com.flight_booking.flight_service.infrastructure.messaging.kafkaSender.FlightKafkaSender;
import com.flight_booking.flight_service.presentation.request.FlightRequestDto;
import com.flight_booking.flight_service.presentation.response.FlightResponseDto;
import com.flight_booking.flight_service.presentation.response.SeatResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlightService {

  private final FlightRepository flightRepository;
  private final AirportService airportService;
  private final SeatService seatService;
  private final FlightKafkaSender flightKafkaSender;

  @Transactional(readOnly = true)
  public FlightResponseDto getFlightById(UUID flightId) {
    Flight flight = flightRepository.findByFlightIdAndIsDeletedFalse(flightId).orElseThrow(
        () -> new RuntimeException("해당하는 항공편이 존재하지 않습니다.")
    );

    return FlightResponseDto.from(flight);
  }

  @Transactional(readOnly = true)
  public PagedModel<FlightResponseDto> getFlightsPage(List<UUID> uuidList, Predicate predicate,
      Pageable pageable) {

    Page<FlightResponseDto> flightResponseDtoPage
        = flightRepository.findAll(uuidList, predicate, pageable);

    return FlightResponseDto.from(flightResponseDtoPage);
  }

  @Transactional
  public FlightResponseDto createFlight(FlightRequestDto requestDto) {

    Airport departureAirport = airportService.getAirportByCityName(requestDto.departureAirport());
    Airport arrivalAirport = airportService.getAirportByCityName(requestDto.arrivalAirport());

    Flight flight = Flight.builder()
        .departureTime(requestDto.departureTime())
        .departureAirport(departureAirport)
        .arrivalTime(requestDto.arrivalTime())
        .arrivalAirport(arrivalAirport)
        .statusEnum(requestDto.status())
        .airline(requestDto.airline())
        .totalEconomySeatsCount(requestDto.totalEconomySeatsCount())
        .totalBusinessSeatsCount(requestDto.totalBusinessSeatsCount())
        .totalFirstClassSeatsCount(requestDto.totalFirstClassSeatsCount())
        .build();

    Flight savedFlight = flightRepository.save(flight);

    seatService.createSeat(savedFlight);

    return FlightResponseDto.from(savedFlight);
  }

  @Transactional
  public FlightResponseDto updateFlight(UUID flightId, FlightRequestDto requestDto) {
    Flight flight = getFlight(flightId);

    flight.update(requestDto.departureTime(), requestDto.arrivalTime(),
        requestDto.status(), requestDto.totalEconomySeatsCount(),
        requestDto.totalBusinessSeatsCount(), requestDto.totalFirstClassSeatsCount());

    return FlightResponseDto.from(flight);
  }

  @Transactional
  public FlightResponseDto deleteFlight(UUID flightId) {

    Flight flight = getFlight(flightId);

    //TODO: 유저 구현 후 수정
    String deletedBy = "tmpUser";

    flight.delete(deletedBy);
    seatService.deleteFlightSeats(flightId, deletedBy);

    return FlightResponseDto.from(flight);
  }

  public Boolean checkFlightStatusBySeatId(UUID seatId) {

    SeatResponseDto seatResponseDto = seatService.getSeat(seatId);

    Flight flight = flightRepository.findById(seatResponseDto.flightId()).orElse(null);

    if (flight == null
        || FlightStatusEnum.DEPARTED.equals(flight.getStatusEnum())
        || FlightStatusEnum.LANDED.equals(flight.getStatusEnum())) {
      return false;
    }

    return true;
  }

  /**
   * flightId로 Flight 조회 메서드
   *
   * @param flightId UUID
   * @return Flight
   */
  private Flight getFlight(UUID flightId) {
    return flightRepository.findByFlightIdAndIsDeletedFalse(flightId).orElseThrow(
        () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
    );
  }
}
