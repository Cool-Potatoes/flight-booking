package com.flight_booking.user_service.infrastructure.configuration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.flight_booking.user_service.infrastructure.security.AuthenticationFilter;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final String[] permitPaths = {
      "/v1/auth/signup",
      "/v1/auth/signin",
      "/v1/auth/fins-id",
      "/v1/users/send-code",
      "/v1/users/verify-code"
  };

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public AuthenticationFilter authenticationFilter() {
    return new AuthenticationFilter(customUserDetailsService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  // SecurityFilterChain 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성화
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))// 세션을 Stateless로 설정
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(permitPaths).permitAll(); // 허용
          auth.anyRequest().authenticated(); // 모든 요청은 인증 필요
        })
        .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}