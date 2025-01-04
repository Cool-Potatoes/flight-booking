package com.flight_booking.gateway_service.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class GatewayConfig {

  private final String[] permitPaths = {
      "/v1/auth/signUp",
      "/v1/auth/login"
  };

  // SecurityFilterChain 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 완전 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성화
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // 세션을 Stateless로 설정
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(permitPaths).permitAll(); // 로그인, 회원가입 URL 허용
          auth.anyRequest().authenticated(); // 나머지 요청은 인증 필요
        });
    return http.build();
  }
}