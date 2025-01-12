package com.flight_booking.user_service.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ResetPwRequest(

    @NotBlank(message = "새로운 비밀번호를 필수로 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호은 최소 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 영어 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    String newPw,

    @NotBlank(message = "새로운 비밀번호 확인을 위해 한 번 더 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호은 최소 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 영어 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    String confirmPw

) {

}
