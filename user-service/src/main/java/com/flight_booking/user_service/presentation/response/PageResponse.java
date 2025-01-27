package com.flight_booking.user_service.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.springframework.data.domain.Page;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
    long totalElements,
    int totalPages,
    int nowPage,
    int size,
    List<T> content
) {

  public static <T> PageResponse<T> from(Page<T> page) {
    return new PageResponse<>(
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize(),
        page.getContent()
    );
  }
}
