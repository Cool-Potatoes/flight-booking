package com.flight_booking.booking_service.infrastructure.repository;

import static com.flight_booking.booking_service.domain.model.QBooking.booking;

import com.flight_booking.booking_service.domain.model.QBooking;
import com.flight_booking.booking_service.presentation.response.BookingResponseCustomDto;
import com.flight_booking.booking_service.presentation.response.QBookingResponseCustomDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
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

    QBooking qBooking = QBooking.booking;

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
        .orderBy(getDynamicSort(sort, booking.getType(), booking.getMetadata()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(booking.count())
        .from(booking)
        .where(builder)
        .fetchOne();

//    JPAQuery<BookingResponseCustomDto> query = queryFactory
//        .select(
//            Projections.fields(
//                BookingResponseCustomDto.class,
//                booking.bookingId,
//                booking.userId,
//                booking.flightId
//                //booking.passengers
//            )
//        )
//        .from(booking);
//    //.where(booking.deletedAt.isNull());
//
//    List<BookingResponseCustomDto> bookings = getQuerydsl().applyPagination(pageable, query)
//        .fetch();
//
//    long totalCount = bookings.size();
//    return new PageImpl<>(bookings, pageable, totalCount);
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
