package com.flight_booking.flight_service.domain.repository;

import com.flight_booking.flight_service.domain.model.Flight;
import com.flight_booking.flight_service.infrastructure.repository.FlightRepositoryCustom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends
    JpaRepository<Flight, UUID>,
    FlightRepositoryCustom {

}
