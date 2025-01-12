package com.flight_booking.user_service.domain.repository;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.infrastructure.repository.UserRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByNameAndPhone(String name, String phone);

  boolean existsByPhone(String phone);
}
