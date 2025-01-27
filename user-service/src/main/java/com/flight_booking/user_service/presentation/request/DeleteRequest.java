package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeleteRequest(

    @NotBlank(message = "현재 비밀번호 확인이 필요합니다.")
    @Size(min = 8, max = 20)
    String password
) {

}
