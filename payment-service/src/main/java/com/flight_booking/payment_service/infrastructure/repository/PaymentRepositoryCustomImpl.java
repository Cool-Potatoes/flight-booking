package com.flight_booking.payment_service.infrastructure.repository;

import com.flight_booking.common.infrastructure.util.DynamicSortUtil;
import com.flight_booking.payment_service.domain.model.QPayment;
import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.flight_booking.payment_service.presentation.response.QPaymentResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public PaymentRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<PaymentResponseDto> findAll(
      List<UUID> uuidList, Predicate predicate, Pageable pageable) {

    QPayment payment = QPayment.payment;

    BooleanBuilder builder = new BooleanBuilder(predicate); // predicate 적용
    if (uuidList != null && !uuidList.isEmpty()) { // idList 값이 있다면 조회
      builder.and(payment.paymentId.in(uuidList));
    }
    builder.and(payment.isDeleted.eq(false)); // isDeleted=false 만 조회

    // size 10, 30, 50 이 아니라면 10으로 고정
    int size = pageable.getPageSize();
    size = (size == 30 || size == 50) ? size : 10;
    pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

    // 입력값이 없다면 생성일순, 수정일순을 기준으로 정렬
    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(
        Sort.Order.desc("createdAt"),
        Sort.Order.desc("updatedAt")
    );

    List<PaymentResponseDto> results = queryFactory
        .select(new QPaymentResponseDto(payment))
        .from(payment)
        .where(builder)
        .orderBy(DynamicSortUtil.getDynamicSort(sort, payment.getType(), payment.getMetadata()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(payment.count())
        .from(payment)
        .where(builder)
        .fetchOne();

    if (total == null) {
      total = 0L;
    }

    return new PageImpl<>(results, pageable, total);
  }

}
