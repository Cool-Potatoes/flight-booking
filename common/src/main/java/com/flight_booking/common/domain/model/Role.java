package com.flight_booking.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

  USER(Authority.USER),
  ADMIN(Authority.ADMIN);

  //유저 권한 조회
  private final String authority;

  //유저 권한
  public static class Authority {

    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
  }

}
