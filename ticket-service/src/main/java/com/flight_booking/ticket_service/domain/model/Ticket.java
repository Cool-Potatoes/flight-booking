package com.flight_booking.ticket_service.domain.model;

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
@Table(name = "p_ticket")
public class Ticket extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID ticketId;

  @Column(nullable = false)
  private UUID bookingId;

  @Column(nullable = false)
  private UUID passengerId;

  @Column(nullable = false)
  private UUID seatId;

  @Column(nullable = false)
  private UUID flightId;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  TicketStateEnum state;

  public void update(UUID seatId) {
    this.seatId = seatId;
  }

  public void updateState(TicketStateEnum ticketStateEnum) {
    this.state = ticketStateEnum;
  }
}
