package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendCodeRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Size(max = 100)
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email

) {

}
