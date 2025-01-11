package com.flight_booking.payment_service.domain.repository;

import com.flight_booking.payment_service.domain.model.Payment;
import com.flight_booking.payment_service.domain.model.QPayment;
import com.flight_booking.payment_service.infrastructure.repository.PaymentRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface PaymentRepository extends JpaRepository<Payment, UUID>,
    PaymentRepositoryCustom,
    QuerydslPredicateExecutor<Payment>,
    QuerydslBinderCustomizer<QPayment> {

  @Override // Predicate 조건
  default void customize(QuerydslBindings querydslBindings, @NotNull QPayment qPayment) {
    querydslBindings.bind(String.class)
        .all((StringPath path, Collection<? extends String> values) -> {
          List<String> valueList = new ArrayList<>(values.stream().map(String::trim).toList());
          if (valueList.isEmpty()) {
            return Optional.empty();
          }
          BooleanBuilder booleanBuilder = new BooleanBuilder();
          for (String s : valueList) {
            booleanBuilder.or(path.containsIgnoreCase(s));
          }
          return Optional.of(booleanBuilder);
        });
  }

  boolean existsByBookingId(UUID bookingId);

  Optional<Payment> findByPaymentIdAndIsDeletedFalse(UUID paymentId);

  @Lock(LockModeType.PESSIMISTIC_WRITE) // 수정 시도시 다른 트랙잭션의 읽기 작업이 끝나지 않았다면 대기
  @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId AND p.isDeleted = false")
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")}) // 5초 대기
  Optional<Payment> findByPaymentIdWithLock(UUID paymentId);

  Optional<Payment> findPaymentByBookingId(UUID bookingId);
}
