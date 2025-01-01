package com.flight_booking.flight_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "p_seat")
public class Seat extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID seatId;

  @Column(nullable = false, length = 10)
  private String seatNumber;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private SeatClassEnum seatClass;

  @Column(nullable = false)
  private Integer price;

  @Column(nullable = false)
  private Boolean status;

  //TODO : 매핑이나 ORM 에 대해 추후 생각 후 다시 변경
  @Column(nullable = false)
  private UUID flightId;


}
