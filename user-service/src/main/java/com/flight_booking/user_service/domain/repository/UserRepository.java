package com.flight_booking.user_service.domain.repository;

import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.infrastructure.repository.UserRepositoryCustom;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByNameAndPhone(String name, String phone);

  boolean existsByPhone(String phone);

  Optional<User> findByEmailAndIsDeletedFalse(String email);

  @Lock(LockModeType.PESSIMISTIC_WRITE) // Pessimistic Write Lock
  @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")}) // 5초 대기
  Optional<User> findByEmailWithLock(String email);
}
