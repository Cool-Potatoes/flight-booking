package com.flight_booking.booking_service.domain.model;

import com.flight_booking.booking_service.infrastructure.client.Passenger;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "p_booking")
public class Booking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID bookingId;

  @Column
  private Long userId;

  @Column
  private UUID flightId;

  @Column
  @Enumerated(value = EnumType.STRING)
  private BookingStatusEnum bookingStatus;

  // TODO : 임시
  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Passenger> passengers;

  public void updateBooking(UUID flightId, BookingStatusEnum bookingStatus,
      List<Passenger> passengers) {

    if (flightId != null) {
      this.flightId = flightId;
    }
    if (bookingStatus != null) {
      this.bookingStatus = bookingStatus;
    }
    if (passengers != null) {
      this.passengers = passengers;
    }
  }

  public void deleteBooking() {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
// TODO
    //    this.deletedBy =
  }

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
