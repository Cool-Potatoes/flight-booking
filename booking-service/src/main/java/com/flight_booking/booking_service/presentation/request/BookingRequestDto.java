package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.booking_service.domain.model.BookingStatusEnum;
import com.flight_booking.booking_service.infrastructure.client.Passenger;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record BookingRequestDto(
    UUID flightId,
    List<Passenger> passengers,
    BookingStatusEnum bookingStatus
) {
}
