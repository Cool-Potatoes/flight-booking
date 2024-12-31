package com.flight_booking.booking_service.domain.repository;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.infrastructure.repository.BookingRepositoryCustom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID>, BookingRepositoryCustom {

}
