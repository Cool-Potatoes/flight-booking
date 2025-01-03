package com.flight_booking.user_service.infrastructure.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private static final int STRENGTH = 10;

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(STRENGTH);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  // SecurityFilterChain 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 비활성화
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // 세션을 Stateless로 설정
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/v1/auth/signUp", "/v1/auth/login").permitAll(); // 로그인, 회원가입 URL 허용
          auth.anyRequest().authenticated(); // 모든 요청은 인증 필요
        })
        .addFilterBefore(new AuthorizationFilter(jwtUtil, customUserDetailsService), // JWT 인가 필터 추가
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}