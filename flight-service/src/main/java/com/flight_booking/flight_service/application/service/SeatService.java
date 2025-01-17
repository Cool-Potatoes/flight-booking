package com.flight_booking.flight_service.application.service;

import com.flight_booking.common.application.dto.BookingCreateRequestDto;
import com.flight_booking.common.application.dto.BookingProcessRequestDto;
import com.flight_booking.common.application.dto.BookingStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.PassengerIsdeletedUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.PaymentRequestDto;
import com.flight_booking.common.application.dto.PaymentStatusUpdateRefundRequestDto;
import com.flight_booking.common.application.dto.ReBookingRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckForRebookRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityCheckRequestDto;
import com.flight_booking.common.application.dto.SeatAvailabilityUpdateTrueRequestDto;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
import com.flight_booking.common.domain.model.BookingStatusEnum;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.domain.model.Seat;
import com.flight_booking.flight_service.domain.model.SeatClassEnum;
import com.flight_booking.flight_service.domain.repository.SeatRepository;
import com.flight_booking.flight_service.infrastructure.messaging.kafkaSender.SeatKafkaSender;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;
  private final SeatKafkaSender seatKafkaSender;
  private final UserService userService;

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
  public SeatResponseDto getSeat(UUID seatId) {

    Seat seat = seatRepository.findById(seatId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 seatId"));

    return SeatResponseDto.from(seat);
  }

  @Transactional(readOnly = true)
  public PagedModel<SeatResponseDto> getSeatsPage(
      UUID flightId, List<UUID> seatIdList, Predicate predicate, Pageable pageable) {

    Page<SeatResponseDto> seatResponseDtoPage
        = seatRepository.findAll(flightId, seatIdList, predicate, pageable);

    return SeatResponseDto.fromPage(seatResponseDtoPage);

  }


  @Transactional
  public void consumeSeatAvailabilityCheckAndUpdate(
      SeatAvailabilityCheckRequestDto seatAvailabilityCheckRequestDto) {

    // TODO 조회 할 때 lock?
    List<UUID> seatIdList = seatAvailabilityCheckRequestDto.seatIdList();

    List<Seat> seatList = seatRepository.findAllById(seatIdList).stream()
        .filter(seat -> seat.getIsAvailable() && !seat.getIsDeleted()).collect(Collectors.toList());

    // 받아온 seatIdList가 유효한지 확인
    if ((seatList.size() == seatIdList.size()) && checkSeatListAvailable(seatList)) {

      Long totalPrice = 0L;
      for (Seat seat : seatList) {
        seat.updateAvailable(false);
        totalPrice += seat.getPrice();
      }

      seatKafkaSender.sendMessage(
          "payment-creation-topic",
          seatAvailabilityCheckRequestDto.bookingId().toString(),
          new PaymentRequestDto(
              seatAvailabilityCheckRequestDto.email(),
              null,
              seatAvailabilityCheckRequestDto.bookingId(),
              totalPrice),
          StackTraceUtils.getCurrentMethodName(),
          StackTraceUtils.getCurrentClassName()
      );

    } else {

      seatKafkaSender.sendMessage(
          "booking-fail-topic",
          seatAvailabilityCheckRequestDto.bookingId().toString(),
          new BookingProcessRequestDto(
              null,
              seatAvailabilityCheckRequestDto.bookingId(),
              null),
          StackTraceUtils.getCurrentMethodName(),
          StackTraceUtils.getCurrentClassName()
      );

    }
  }


  @Transactional
  public void consumeSeatAvailabilityCheckAndUpdateForRebook(
      SeatAvailabilityCheckForRebookRequestDto requestDto) {

    Seat seat = getSeatEntity(requestDto.seatId());

    seat.updateAvailable(false);

    seatKafkaSender.sendMessage(
        "payment-creation-topic",
        seat.getSeatId().toString(),
        new PaymentRequestDto(
            requestDto.email(),
            requestDto.ticketId(),
            requestDto.bookingId(),
            seat.getPrice()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );
  }

  // 동기
  @Transactional(readOnly = false)
  public Long updateSeatAvailableFalseAndGetSeatPrice(UUID seatId) {

    Seat seat = getSeatIsDeletedFalse(seatId);

    if (!seat.getIsAvailable()) {
      // TODO
      // 실패 로직.. 동기화? 비동기화?
      throw new RuntimeException("새로운 좌석이 이미 예약되었습니다: " + seat.getSeatId());
    } else {
      return seat.getPrice();
    }
  }

  @Transactional(readOnly = false)
  public void seatAvailabilityUpdateTrue(SeatAvailabilityUpdateTrueRequestDto requestDto) {

    Seat seat = getSeatEntity(requestDto.seatId());

    seat.updateAvailable(requestDto.available());
  }

  public Boolean getSeatIsAvailable(UUID seatId) {

    Seat seat = getSeatEntity(seatId);

    return seat.getIsAvailable();
  }

  public void seatCalculateDifferenceAndRefund(
      SeatCalculateDifferenceAndRefundRequestDto seatBookingRequestDto) {

    Long difference = calculateDifferenceOldAndNewSeatPrice(seatBookingRequestDto);

    boolean successRefund = userService.RefundMileage(seatBookingRequestDto.email(),
        seatBookingRequestDto.role(),
        seatBookingRequestDto.email(), difference);

    // redis 사용하면 된다
    if (successRefund) {
      seatKafkaSender.sendMessage(
          "create-booking-topic",
          // 하나의 키는 하나의 파티션으로 고정됨, 자연스럽게 동시성 처리가 되는데
          // 그거 고려해서 만들면 됨
          // 처리되는쪽 기준으로
          // 컨슈머에서 해당 토픽을 소모하니까 그거 기준으로 생각
          seatBookingRequestDto.passengerRequestDto().seatId().toString(),
          new BookingCreateRequestDto(
              new ReBookingRequestDto(seatBookingRequestDto.passengerRequestDto(),
                  seatBookingRequestDto.ticketId()),
              seatBookingRequestDto.email()),
          StackTraceUtils.getCurrentMethodName(),
          StackTraceUtils.getCurrentClassName()
      );

      // todo : 다른 kafka 메시지 보내기,
      //  ticket에도 보내서 상태업데이트 + lock 해제
      sendKafkaMessagesForUpdateStatusToRefund(seatBookingRequestDto.bookingId(),
          seatBookingRequestDto.seatId(), seatBookingRequestDto.passengerId());
    }

  }

  private void sendKafkaMessagesForUpdateStatusToRefund(UUID bookingId, UUID seatId,
      UUID passengerId) {

    seatKafkaSender.sendMessage("booking-status-update-refund-topic",
        bookingId.toString(),
        new BookingStatusUpdateRefundRequestDto(bookingId,
            BookingStatusEnum.BOOKING_REFUND_COMPLETE), StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());

    seatKafkaSender.sendMessage("seat-availability-update-true-topic",
        seatId.toString(),
        new SeatAvailabilityUpdateTrueRequestDto(seatId, true),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

    seatKafkaSender.sendMessage("passenger-isdeleted-update-true-topic",
        passengerId.toString(),
        new PassengerIsdeletedUpdateTrueRequestDto(passengerId, true),
        StackTraceUtils.getCurrentMethodName(), StackTraceUtils.getCurrentClassName());

    // payment는 bookingId 전송해서 찾아서 처리
    seatKafkaSender.sendMessage("payment-status-update-refund-topic",
        bookingId.toString(),
        new PaymentStatusUpdateRefundRequestDto(bookingId,
            PaymentStatusEnum.REFUND_COMPLETE), StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());

  }

  private Long calculateDifferenceOldAndNewSeatPrice(
      SeatCalculateDifferenceAndRefundRequestDto seatBookingRequestDto) {

    Seat oldSeat = getSeatEntity(seatBookingRequestDto.seatId());

    Seat newSeat = getSeatEntity(seatBookingRequestDto.passengerRequestDto().seatId());

    return oldSeat.getPrice() - newSeat.getPrice();
  }

  private Seat getSeatIsDeletedFalse(UUID seatId) {

    return seatRepository.findBySeatIdAndIsDeletedFalse(seatId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 seatId"));
  }

  private Seat getSeatEntity(UUID seatId) {

    return seatRepository.findBySeatIdAndIsDeletedFalse(seatId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 seatId"));
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

  private boolean checkSeatListAvailable(List<Seat> seatList) {
    for (Seat seat : seatList) {
      if (!seat.getIsAvailable()) {
        return false;
      }
    }
    return true;
  }


}