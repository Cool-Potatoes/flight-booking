package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignInRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Size(max = 100)
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20)
    String password

) {

}
