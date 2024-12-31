package com.flight_booking.booking_service.infrastructure.repository;

import static com.flight_booking.booking_service.domain.model.QBooking.booking;

import com.flight_booking.booking_service.domain.model.Booking;
import com.flight_booking.booking_service.presentation.response.BookingResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl extends QuerydslRepositorySupport implements
    BookingRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public BookingRepositoryImpl(JPAQueryFactory queryFactory) {
    super(Booking.class);
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<BookingResponse> findAll(Pageable pageable, Integer size) {

    pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

    JPAQuery<BookingResponse> query = queryFactory
        .select(
            Projections.fields(
                BookingResponse.class,
                booking.bookingId,
                booking.userId,
                booking.flightId
                //booking.passengers
                )
        )
        .from(booking);
        //.where(booking.deletedAt.isNull());

    List<BookingResponse> bookings = getQuerydsl().applyPagination(pageable, query).fetch();

    long totalCount = bookings.size();

    return new PageImpl<>(bookings, pageable, totalCount);
  }

  @Override
  public BookingResponse findByBookingId(UUID bookingId) {
    return queryFactory
        .select(
            Projections.fields(
                BookingResponse.class,
                booking.userId,
                booking.bookingId,
                booking.flightId,
                booking.passengers
            )
        )
        .from(booking)
        .where(
            booking.deletedAt.isNull()
                .and(booking.bookingId.eq(bookingId))
        )
        .fetchOne();
  }
}
