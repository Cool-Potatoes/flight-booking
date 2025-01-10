package com.flight_booking.common.infrastructure.security;

import com.flight_booking.common.domain.model.UserRoleEnum;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(
    String email,
    UserRoleEnum role
) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.createAuthorityList(this.role.getAuthority());
  }

  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // 계정 만료 여부. 예시로 true 반환.
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // 계정 잠금 여부. 예시로 true 반환.
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // 자격 증명 만료 여부. 예시로 true 반환.
  }

  @Override
  public boolean isEnabled() {
    return true; // 계정 활성화 여부. 예시로 true 반환.
  }
}