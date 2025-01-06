package com.flight_booking.booking_service.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  @OneToMany(mappedBy = "booking")
  private List<Passenger> passengers = new ArrayList<>();

  public void updateBooking(UUID flightId, BookingStatusEnum bookingStatus) {

    if (flightId != null) {
      this.flightId = flightId;
    }
    if (bookingStatus != null) {
      this.bookingStatus = bookingStatus;
    }
  }

  public void deleteBooking() {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
    // TODO
    //    this.deletedBy =
  }
}
