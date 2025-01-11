package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlockRequest(

    @NotBlank(message = "블락 이유는 필수 항목입니다.")
    @Size(max = 255, message = "최대 255자까지 입력할 수 있습니다.")
    String reason

) {

}
