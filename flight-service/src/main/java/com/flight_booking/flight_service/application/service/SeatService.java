package com.flight_booking.flight_service.application.service;

import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.domain.model.Seat;
import com.flight_booking.flight_service.domain.model.SeatClassEnum;
import com.flight_booking.flight_service.domain.repository.SeatRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;

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

}
