package com.flight_booking.user_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@AllArgsConstructor
public enum Role {

  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String role;

  public SimpleGrantedAuthority toAuthority() {
    return new SimpleGrantedAuthority(role);
  }
}
