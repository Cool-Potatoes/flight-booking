package com.flight_booking.flight_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "p_flight")
public class Flight extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID flightId;

  @Column(nullable = false)
  private LocalDateTime departureTime;

  @Column(nullable = false)
  private LocalDateTime arrivalTime;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private FlightStatusEnum statusEnum;

  @Column(nullable = false)
  private Integer totalEconomySeatsCount;

  @Column(nullable = false)
  private Integer totalBusinessSeatsCount;

  @Column(nullable = false)
  private Integer totalFirstClassSeatsCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "departure_airport_id", nullable = false)
  private Airport departureAirport;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "arrival_airport_id", nullable = false)
  private Airport arrivalAirport;

  @Column(nullable = false, length = 10)
  private String airline;

  public void update(
      LocalDateTime departureTime,
      LocalDateTime arrivalTime,
      FlightStatusEnum status, Integer totalEconomySeatsCount, Integer totalBusinessSeatsCount,
      Integer totalFirstClassSeatsCount) {
    if (departureTime != null) {
      this.departureTime = departureTime;
    }
    if (arrivalTime != null) {
      this.arrivalTime = arrivalTime;
    }
    if (status != null) {
      this.statusEnum = status;
    }
    if (totalEconomySeatsCount != null) {
      this.totalEconomySeatsCount = totalEconomySeatsCount;
    }
    if (totalBusinessSeatsCount != null) {
      this.totalBusinessSeatsCount = totalBusinessSeatsCount;
    }
    if (totalFirstClassSeatsCount != null) {
      this.totalFirstClassSeatsCount = totalFirstClassSeatsCount;
    }
  }

  public void delete(String deletedBy) {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = deletedBy;
  }
}
