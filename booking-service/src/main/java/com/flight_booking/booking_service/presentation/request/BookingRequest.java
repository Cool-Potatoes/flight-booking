package com.flight_booking.booking_service.presentation.request;

import com.flight_booking.booking_service.infrastructure.client.Passenger;
import com.flight_booking.booking_service.infrastructure.client.PassengerTypeEnum;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

  private UUID flightId;
  private List<Passenger> passengers;
//  private PassengerTypeEnum passengerType;
//  private String passengerName;
//  private Boolean baggage;
//  private Boolean meal;
}
