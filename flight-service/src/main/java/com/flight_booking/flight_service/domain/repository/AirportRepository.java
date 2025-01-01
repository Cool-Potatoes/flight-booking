package com.flight_booking.flight_service.domain.repository;

import com.flight_booking.flight_service.domain.model.Airport;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, UUID> {

}

