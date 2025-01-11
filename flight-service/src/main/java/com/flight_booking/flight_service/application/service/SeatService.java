package com.flight_booking.flight_service.application.service;

import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingSeatCheckRequestDto;
import com.flight_booking.common.application.dto.PassengerRequestDto;
import com.flight_booking.common.application.dto.PaymentRefundRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityChangeRequestDto;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
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
  public void consumeSeatAvailabilityCheckAndUpdate(SeatBookingRequestDto seatBookingRequestDto) {

    // TODO 조회 할 때 lock?
    List<UUID> seatIdList = seatBookingRequestDto.seatIdList();

    List<Seat> seatList = seatRepository.findAllById(seatIdList).stream()
        .filter(seat -> seat.getIsAvailable() && !seat.getIsDeleted()).collect(Collectors.toList());

    // 받아온 seatIdList가 유효한지 확인
    if ((seatList.size() == seatIdList.size()) && checkSeatListAvailable(seatList)) {

      Long totalPrice = 0L;
      for (Seat seat : seatList) {
        seat.updateAvailable(false);
        totalPrice += seat.getPrice();
      }

      kafkaTemplate.send("payment-creation-topic", seatBookingRequestDto.bookingId().toString(),
          ApiResponse.ok(
              new PaymentRequestDto(seatBookingRequestDto.email(),
                  seatBookingRequestDto.bookingId(),
                  totalPrice),
              "message from consumeSeatAvailabilityCheckAndUpdate"));
    } else {

      kafkaTemplate.send("booking-fail-topic", seatBookingRequestDto.bookingId().toString(),
          ApiResponse.of(
              new BookingProcessRequestDto(seatBookingRequestDto.bookingId(), null),
              "좌석이 이미 예약되었습니다.",
              HttpStatus.BAD_REQUEST
          ));
    }
  }

  private boolean checkSeatListAvailable(List<Seat> seatList) {
    for (Seat seat : seatList) {
      if (!seat.getIsAvailable()) {
        return false;
      }
    }
    return true;
  }

  @Transactional(readOnly = false)
  public void changeSeatAvailability(
      SeatAvailabilityChangeRequestDto seatAvailabilityChangeRequestDto) {

    Long newSeatTotalPrice = 0L;

    for (PassengerRequestDto dto : seatAvailabilityChangeRequestDto.passengerRequestDtos()) {
      Seat seat = seatRepository.findById(dto.seatId()).orElseThrow(IllegalArgumentException::new);
      if (!seat.getIsAvailable()) {
        throw new RuntimeException("새로운 좌석이 이미 예약되었습니다: " + seat.getSeatId());
      }
      if (seat.getIsDeleted()) {
        throw new RuntimeException("삭제된 좌석입니다.");
      }
      // TODO : 여기서 미리 좌석을 false로 바꿔놓는다면
      //  -> 새로운 로직을 만들어서 좌석 예매 구현
      //  만약 false로 바꾸지 않고 Lock 을 통해서 이 좌석을 건들지 못하게 한다면?
      //  -> 기존 로직 재활용 가능?
//      seat.updateAvailable(false);
      newSeatTotalPrice += seat.getPrice();
    }

    kafkaTemplate.send("payment-refund-topic",
        seatAvailabilityChangeRequestDto.bookingId().toString(),
        ApiResponse.ok(
            new PaymentRefundRequestDto(seatAvailabilityChangeRequestDto.email(),
                seatAvailabilityChangeRequestDto.bookingId(),
                seatAvailabilityChangeRequestDto.passengerRequestDtos(),
                newSeatTotalPrice),
            "message from checkSeatAvailable"));
  }
}