package com.flight_booking.booking_service.infrastructure.repository;

import com.flight_booking.booking_service.domain.model.QBooking;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.QBookingResponseCustomDto;
import com.flight_booking.common.infrastructure.util.DynamicSortUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl implements
    BookingRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public BookingRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<BookingResponseCustomDto> findAllBookings(Predicate predicate, Pageable pageable) {

    QBooking booking = QBooking.booking;

    BooleanBuilder builder = new BooleanBuilder(predicate); // predicate 적용 - 키워드 검색
    builder.and(booking.isDeleted.eq(false)); // isDeleted=false 만 조회

    int size = pageable.getPageSize();
    size = (size == 30 || size == 50) ? size : 10;
    pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(
        Sort.Order.desc("createdAt"),
        Sort.Order.desc("updatedAt")
    );

    List<BookingResponseCustomDto> results = queryFactory
        .select(new QBookingResponseCustomDto(booking))
        .from(booking)
        .where(builder)
        .orderBy(DynamicSortUtil.getDynamicSort(sort, booking.getType(), booking.getMetadata()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(booking.count())
        .from(booking)
        .where(builder)
        .fetchOne();

    if (total == null) {
      total = 0L;
    }

    return new PageImpl<>(results, pageable, total);

  }

}
