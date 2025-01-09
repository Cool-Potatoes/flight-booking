package com.flight_booking.user_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Setter;

@Setter
@Embeddable
public class BlockedInfo {

  @Column(name = "blocked_at")
  private LocalDateTime blockedAt;

  @Column(name = "blocked_reason")
  private String blockedReason;

  @Column(name = "blocked_by", length = 100)
  private String blockedBy;

}
