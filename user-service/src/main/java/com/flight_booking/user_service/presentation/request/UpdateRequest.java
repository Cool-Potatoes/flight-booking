package com.flight_booking.user_service.presentation.request;

import com.flight_booking.user_service.domain.model.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRequest(
    @Size(min = 1, max = 30, message = "이름은 최소 1자 이상, 30자 이하여야 합니다.")
    String name,

    @Size(min = 12, max = 15)
    @Pattern(regexp = "^[0-9]{3}[-][0-9]{3,4}[-][0-9]{4}$",
        message = "유효한 전화번호 형식이 아닙니다.")
    String phone,

    Role role   // 관리자만 수정 가능
) {

}
