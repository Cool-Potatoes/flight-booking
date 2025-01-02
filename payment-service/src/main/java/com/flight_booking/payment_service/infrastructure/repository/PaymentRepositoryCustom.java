package com.flight_booking.payment_service.infrastructure.repository;

import com.flight_booking.payment_service.presentation.response.PaymentResponseDto;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepositoryCustom {

  Page<PaymentResponseDto> findAll(List<UUID> uuidList, Predicate predicate, Pageable pageable);

}
