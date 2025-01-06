package com.flight_booking.booking_service.domain.repository;

import com.flight_booking.booking_service.domain.model.Passenger;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {

  List<Passenger> findAllByBooking_BookingId(UUID bookingId);
}