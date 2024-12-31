package com.flight_booking.payment_service.presentation.request;

import java.util.UUID;

public record PaymentRequestDto(UUID bookingId, Integer fare) {
}
