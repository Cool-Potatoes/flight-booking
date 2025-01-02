package com.flight_booking.booking_service.infrastructure.client;

import com.flight_booking.booking_service.domain.model.Booking;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_passenger")
public class Passenger {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "passenger_id", updatable = false, nullable = false)
  private UUID passengerId;

  private UUID seatId;
  private PassengerTypeEnum passengerType;
  private String passengerName;
  private Boolean baggage;
  private Boolean meal;

  // TODO : 임시
  @ManyToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;
}
