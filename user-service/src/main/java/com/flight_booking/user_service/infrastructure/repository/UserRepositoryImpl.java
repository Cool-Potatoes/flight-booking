package com.flight_booking.user_service.infrastructure.repository;

import com.flight_booking.user_service.domain.model.QUser;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public UserRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<UserListResponse> findAll(
      String email, String name, String role, Boolean isBlocked, Pageable pageable) {
    QUser user = QUser.user;

    // 1. 동적 조건 생성
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(user.isDeleted.eq(false));

    // 2. 조건에 맞는 값이 있을 때만 추가
    if (email != null && !email.isEmpty()) {
      builder.and(user.email.containsIgnoreCase(email));  // 이메일로 검색
    }
    if (name != null && !name.isEmpty()) {
      builder.and(user.name.containsIgnoreCase(name));  // 이름으로 검색
    }
    if (role != null && !role.isEmpty()) {
      builder.and(user.role.eq(Role.valueOf(role)));  // 역할로 검색
    }
    if (isBlocked != null) {
      builder.and(user.isBlocked.eq(isBlocked));  // 차단 여부로 검색
    }

    // 3. 페이지 크기 고정 (10, 30, 50 중 하나)
    int size = pageable.getPageSize();
    size = (size == 30 || size == 50) ? size : 10;
    pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

    // 4. 기본 정렬 조건
    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(
        Sort.Order.desc("createdAt"),
        Sort.Order.desc("updatedAt")
    );

    // 5. 데이터 조회
    List<UserListResponse> users = queryFactory
        .select(Projections.constructor(UserListResponse.class,
            user.id,
            user.email,
            user.name,
            user.role,
            user.isBlocked,
            user.createdAt))
        .from(user)
        .where(builder) // 동적으로 생성된 조건 사용
        .orderBy(getDynamicSort(sort, user.getType(), user.getMetadata()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 6. 전체 데이터 수 조회
    Long total = queryFactory
        .select(user.count())
        .from(user)
        .where(builder) // 동적으로 생성된 조건 사용
        .fetchOne();

    if (total == null) {
      total = 0L;
    }

    // 6. 페이징된 결과 반환
    return new PageImpl<>(users, pageable, total);
  }


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
