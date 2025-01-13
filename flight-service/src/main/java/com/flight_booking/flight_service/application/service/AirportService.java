package com.flight_booking.flight_service.application.service;

import com.flight_booking.flight_service.domain.model.Airport;
import com.flight_booking.flight_service.domain.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirportService {

  private final AirportRepository airportRepository;

  public Airport getAirportByCityName(String cityName) {

    Airport airport = airportRepository.findByCityName(cityName)
        .orElseThrow(
            //TODO: Error 타입 정해지면 수정
            () -> new RuntimeException("해당하는 공항이 존재하지 않습니다.")
        );

    return airport;
  }

}
