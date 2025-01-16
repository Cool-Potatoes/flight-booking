package com.flight_booking.common.application.dto;

import java.util.UUID;

public record ReBookingRequestDto(
    PassengerRequestDto passengerRequestDto,
    UUID ticketId
) {

}
