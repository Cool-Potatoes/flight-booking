package com.flight_booking.user_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.springframework.data.domain.Page;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
    long totalElements, // 총 데이터 수
    int totalPages, // 총 페이지 수
    int nowPage, // 현재 페이지
    int size,   // 페이지 크기
    List<T> content       // 실제 데이터
) {

  public static <T> PageResponse<T> from(Page<T> page) {
    return new PageResponse<>(
        page.getTotalElements(), // 총 데이터 수
        page.getTotalPages(),    // 총 페이지 수
        page.getNumber(),        // 현재 페이지
        page.getSize(),          // 페이지 크기
        page.getContent()        // 실제 데이터
    );
  }
}
