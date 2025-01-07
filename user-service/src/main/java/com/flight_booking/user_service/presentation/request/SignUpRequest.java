package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUpRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Size(max = 100)
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호은 최소 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 영어 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    String password,

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
