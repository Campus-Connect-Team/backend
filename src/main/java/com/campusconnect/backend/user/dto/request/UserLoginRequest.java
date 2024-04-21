package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginRequest {

    @NotEmpty(message = "학번 입력은 필수 사항입니다.")
    @Pattern(regexp = "[0-9]+", message = "학번은 0~9까지 숫자로만 입력할 수 있습니다.")
    @Size(min = 8, max = 8, message = "학번은 8자리로 입력해야 합니다.")
    private String studentNumber;

    @NotEmpty(message = "비밀번호 입력은 필수 사항입니다.")
    private String password;
}
