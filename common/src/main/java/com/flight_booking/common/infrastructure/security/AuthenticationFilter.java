package com.flight_booking.common.infrastructure.security;

import com.flight_booking.common.domain.model.UserRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "사용자 인증 필터")
public class AuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 헤더에서 사용자 정보 추출
    String email = request.getHeader("X-USER-EMAIL");
    String header = request.getHeader("X-USER-ROLE");

    log.info("header: {}, {}", email, header);

    if (email != null && header != null) {
      try {
        // "ROLE_" 접두어 제거
        String roleName = header.replace("ROLE_", "");

        // UserRoleEnum에 맞게 변환
        UserRoleEnum role = UserRoleEnum.valueOf(roleName);

        // 이메일로 사용자 정보 로드
        UserDetails userDetails = new CustomUserDetails(email, role);

        // 인증 객체 생성 (권한 포함)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        log.info("auth {}", authentication.getAuthorities());

        // 인증 정보를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {

        log.error("사용자 인증 실패: {}", e.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("사용자 인증 실패");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}