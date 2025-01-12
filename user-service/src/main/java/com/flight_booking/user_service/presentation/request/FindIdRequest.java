package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FindIdRequest(

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 1, max = 30, message = "이름은 최소 1자 이상, 30자 이하여야 합니다.")
    String name,

    @NotBlank(message = "전화번호는 필수입니다.")
    @Size(min = 12, max = 15)
    @Pattern(regexp = "^[0-9]{3}[-][0-9]{3,4}[-][0-9]{4}$",
        message = "유효한 전화번호 형식이 아닙니다.")
    String phone

) {

}
