package com.flight_booking.booking_service.domain.model;

import com.flight_booking.booking_service.common.model.BaseEntity;
import com.flight_booking.booking_service.infrastructure.client.Passenger;
import com.flight_booking.booking_service.infrastructure.client.PassengerTypeEnum;
import com.flight_booking.booking_service.infrastructure.client.SeatClassEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "p_bookings")
public class Booking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "booking_id", updatable = false, nullable = false)
  private UUID bookingId;

  @Column
  private Long userId;

  @Column
  private UUID flightId;

  // TODO : 임시
  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Passenger> passengers;

//  @Column
//  private UUID passengerId;

//  @Column
//  private SeatClassEnum seatClassEnum;
//
//  @Column
//  private String seatNumber;
//
//  @Column
//  private Integer seatPrice;
//
//  @Column
//  private PassengerTypeEnum passengerTypeEnum;
//
//  @Column
//  private String passengerName;
//
//  @Column
//  private Boolean baggage;
//
//  @Column
//  private Boolean meal;
}
