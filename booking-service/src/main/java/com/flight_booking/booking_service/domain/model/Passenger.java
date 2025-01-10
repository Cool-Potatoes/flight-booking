package com.flight_booking.booking_service.domain.model;

import com.flight_booking.common.domain.model.PassengerTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Passenger extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID passengerId;

  @Column(nullable = false)
  private UUID seatId;

  @Column(nullable = false)
  private String passengerName;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private PassengerTypeEnum passengerType;

  @Column(nullable = false)
  private Boolean baggage;

  @Column(nullable = false)
  private Boolean meal;

  @ManyToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;

  public void updatePassenger(Booking booking, UUID seatId, PassengerTypeEnum passengerType,
      String passengerName, Boolean baggage, Boolean meal) {
    this.booking = booking;
    this.seatId = seatId;
    this.passengerType = passengerType;
    this.passengerName = passengerName;
    this.baggage = baggage;
    this.meal = meal;
  }
}
