package com.flight_booking.booking_service.infrastructure.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
  }

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> {
      //TODO : 추후 유저 구현되면 수정
      String userId = "tmpUser";

      return Optional.of(userId);
    };
  }
}
