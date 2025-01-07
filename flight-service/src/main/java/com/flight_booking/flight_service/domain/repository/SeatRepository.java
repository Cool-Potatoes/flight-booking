package com.flight_booking.flight_service.domain.repository;

import com.flight_booking.flight_service.domain.model.Seat;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

  Set<Seat> findByFlight_FlightIdAndIsAvailableTrueAndIsDeletedFalse(UUID flightId);
}
