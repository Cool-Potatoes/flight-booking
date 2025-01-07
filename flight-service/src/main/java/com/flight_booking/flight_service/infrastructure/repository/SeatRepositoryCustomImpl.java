package com.flight_booking.flight_service.infrastructure.repository;

import com.flight_booking.flight_service.domain.model.QSeat;
import com.flight_booking.flight_service.presentation.response.QSeatResponseDto;
import com.flight_booking.flight_service.presentation.response.SeatResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SeatRepositoryCustomImpl implements SeatRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public SeatRepositoryCustomImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<SeatResponseDto> findAll(
      UUID flightId, List<UUID> seatIdList, Predicate predicate, Pageable pageable) {

    QSeat seat = QSeat.seat;

    BooleanBuilder builder = new BooleanBuilder(predicate); // predicate 적용
    if (seatIdList != null && !seatIdList.isEmpty()) { // idList 값이 있다면 조회
      builder.and(seat.seatId.in(seatIdList));
    }
    builder.and(seat.isDeleted.eq(false)); // isDeleted=false 만 조회
    builder.and(seat.flight.flightId.eq(flightId)); // flightId로 필터링

    // size 10, 30, 50 이 아니라면 10으로 고정
    int size = pageable.getPageSize();
    size = (size == 30 || size == 50) ? size : 10;
    pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

    // 입력값이 없다면 생성일순, 수정일순을 기준으로 정렬
    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(
        Sort.Order.desc("createdAt"),
        Sort.Order.desc("updatedAt")
    );

    List<SeatResponseDto> results = queryFactory
        .select(new QSeatResponseDto(seat))
        .from(seat)
        .where(builder)
        .orderBy(getDynamicSort(sort, seat.getType(), seat.getMetadata()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(seat.count())
        .from(seat)
        .where(builder)
        .fetchOne();

    if (total == null) {
      total = 0L;
    }

    return new PageImpl<>(results, pageable, total);
  }

  // 정렬(Sort) 정보를 기반으로 Querydsl의 OrderSpecifier 객체들을 동적으로 생성
  // 제네릭을 사용하여 특정 엔터티 클래스에 종속되지 않음
  private <T> OrderSpecifier[] getDynamicSort(Sort sort, Class<? extends T> entityClass,
      PathMetadata pathMetadata) {
    List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

    // 도메인 클래스에 맞는 PathBuilder를 생성
    PathBuilder<Object> pathBuilder = new PathBuilder<>(entityClass, pathMetadata);

    sort.stream().forEach(orderSpecifier -> {
      Order direction = orderSpecifier.isAscending() ? Order.ASC : Order.DESC;
      String prop = orderSpecifier.getProperty();

      // 동적으로 해당 필드에 접근
      orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(prop)));
    });

    return orderSpecifiers.toArray(new OrderSpecifier[0]); // list 크기에 맞춰 배열 생성
  }
}