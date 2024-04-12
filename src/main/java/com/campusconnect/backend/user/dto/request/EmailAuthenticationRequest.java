package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailAuthenticationRequest {

    @NotEmpty(message = "이메일 입력은 필수 사항입니다.")
    private String email;

    @NotEmpty(message = "메일로부터 받은 인증번호를 입력해 주세요.")
    @Size(max = 6, message = "인증번호는 6자리입니다.")
    private String authenticationNumber;
}
