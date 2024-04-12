package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserEmailRequest {

    @NotEmpty(message = "이메일 입력은 필수 사항입니다.")
    private String email;
}
