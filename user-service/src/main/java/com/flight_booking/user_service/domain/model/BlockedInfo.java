package com.flight_booking.user_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class BlockedInfo {

  @Column(name = "blocked_at")
  private LocalDateTime blockedAt;

  @ElementCollection
  @Column(name = "blocked_reason")
  private List<String> blockedReason;

  @Column(name = "blocked_by", length = 100)
  private String blockedBy;

}
