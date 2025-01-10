package com.flight_booking.flight_service.application.service;

import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.SeatBookingRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.domain.model.Seat;
import com.flight_booking.flight_service.domain.model.SeatClassEnum;
import com.flight_booking.flight_service.domain.repository.SeatRepository;
import com.flight_booking.flight_service.presentation.request.SeatRequestDto;
import com.flight_booking.flight_service.presentation.response.SeatResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;
  private final KafkaTemplate<String, ApiResponse<?>> kafkaTemplate;

  public void createSeat(Flight flight) {

    int totalEconomySeatsCount = flight.getTotalEconomySeatsCount();
    int totalBusinessSeatsCount = flight.getTotalBusinessSeatsCount();
    int totalFirstSeatsCount = flight.getTotalFirstClassSeatsCount();

    Set<Seat> seatSet = new HashSet<>();

    createSeatsForClass(flight, seatSet, SeatClassEnum.ECONOMY, totalEconomySeatsCount);
    createSeatsForClass(flight, seatSet, SeatClassEnum.BUSINESS, totalBusinessSeatsCount);
    createSeatsForClass(flight, seatSet, SeatClassEnum.FIRST, totalFirstSeatsCount);

    seatRepository.saveAll(seatSet);
  }

  @Transactional
  public void updateFlightWholeSeatPrice(UUID flightId, SeatRequestDto seatRequestDto) {
    Set<Seat> seatSet = seatRepository.findByFlight_FlightIdAndIsAvailableTrueAndIsDeletedFalse(
        flightId);
    if (seatSet.isEmpty()) {
      throw new RuntimeException("이용 가능한 좌석이 없거나, 유효하지 않은 flightID 입니다.");
    }

    for (Seat seat : seatSet) {
      Long newPrice = seatRequestDto.price();
      // 좌석 클래스에 따라 가격 계산
      if (seat.getSeatClass().equals(SeatClassEnum.ECONOMY)) {
        seat.updatePrice(newPrice);
      } else if (seat.getSeatClass().equals(SeatClassEnum.BUSINESS)) {
        seat.updatePrice((long) (newPrice * 1.5));
      } else if (seat.getSeatClass().equals(SeatClassEnum.FIRST)) {
        seat.updatePrice((long) (newPrice * 3));
      }
    }

  }

  @Transactional
  public void deleteFlightSeats(UUID flightId, String deletedBy) {
    Set<Seat> seatSet = seatRepository.findByFlight_FlightId(flightId);
    for (Seat seat : seatSet) {
      seat.delete(deletedBy);
    }
  }

  @Transactional(readOnly = true)
  public PagedModel<SeatResponseDto> getSeatsPage(
      UUID flightId, List<UUID> seatIdList, Predicate predicate, Pageable pageable) {

    Page<SeatResponseDto> seatResponseDtoPage
        = seatRepository.findAll(flightId, seatIdList, predicate, pageable);

    return SeatResponseDto.fromPage(seatResponseDtoPage);

  }


  // 좌석 클래스에 따른 Seat 생성
  private void createSeatsForClass(Flight flight, Set<Seat> seatSet, SeatClassEnum seatClass,
      int totalSeats) {
    for (int i = 0; i < totalSeats; i++) {
      Seat seat = Seat.builder()
          .seatCode(generateSeatCode(i))
          .seatClass(seatClass)
          .flight(flight)
          .isAvailable(true)
          .price(0L)
          .build();
      seatSet.add(seat);
    }
  }

  // 좌석 코드 생성
  private String generateSeatCode(int index) {
    char rowLetter = (char) ('A' + (index % 7)); // A to G
    int seatNumber = (index % 12) + 1;  // 1 to 12

    return String.format("%c%02d", rowLetter, seatNumber);
  }

  @Transactional
  public void updateSeatAvailable(SeatBookingRequestDto seatBookingRequestDto) {

    Seat seat = seatRepository.findById(seatBookingRequestDto.seatId()).orElseThrow();

    if (seat.getIsDeleted()) {
      throw new RuntimeException("삭제된 좌석입니다.");
    }

    if (seat.getIsAvailable()) {

      seat.updateAvailable(false);

      kafkaTemplate.send("payment-creation-topic", seat.getSeatId().toString(),
          ApiResponse.ok(
              new PaymentRequestDto(seatBookingRequestDto.email(),
                  seatBookingRequestDto.bookingId(),
                  seat.getPrice()),
              "message from createBooking"));
    } else {

      kafkaTemplate.send("payment-creation-fail-topic", seat.getSeatId().toString(),
          ApiResponse.ok(
              new PaymentRequestDto(seatBookingRequestDto.email(),
                  seatBookingRequestDto.bookingId(),
                  seat.getPrice()),
              "message from createBooking"));
    }


  }
}
