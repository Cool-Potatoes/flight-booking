package com.flight_booking.flight_service.presentation.response;

import com.flight_booking.flight_service.domain.model.Seat;
import com.flight_booking.flight_service.domain.model.SeatClassEnum;
import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public record SeatResponseDto(
    UUID seatId,
    String seatCode,
    SeatClassEnum seatClass,
    Long price,
    Boolean isAvailable,
    UUID flightId
) {

  @QueryProjection
  public SeatResponseDto(Seat seat) {
    this(
        seat.getSeatId(),
        seat.getSeatCode(),
        seat.getSeatClass(),
        seat.getPrice(),
        seat.getIsAvailable(),
        seat.getFlight().getFlightId()
    );
  }

  public static SeatResponseDto from(Seat seat) {
    return new SeatResponseDto(seat);
  }

  public static PagedModel<SeatResponseDto> fromPage(Page<SeatResponseDto> seatResponseDtoPage) {
    return new PagedModel<>(seatResponseDtoPage);
  }

}