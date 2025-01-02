package com.flight_booking.booking_service.domain.model;

import com.flight_booking.booking_service.infrastructure.client.Passenger;
import com.flight_booking.booking_service.presentation.request.BookingRequestDto;
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
import java.util.List;
import java.util.Optional;
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

  public void updateBooking(BookingRequestDto requestDto){

    Optional.ofNullable(requestDto.flightId()).ifPresent(flightId -> this.flightId = flightId);
    Optional.ofNullable(requestDto.passengers()).ifPresent(passengers -> this.passengers = passengers);
    Optional.ofNullable(requestDto.bookingStatus()).ifPresent(bookingStatus -> this.bookingStatus = bookingStatus);
  }

  public void deleteBooking(){
    this.isDeleted = true;
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
