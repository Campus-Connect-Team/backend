package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserFindPasswordRequest {

    @NotEmpty(message = "학번 입력은 필수 사항입니다.")
    private String studentNumber;

    @NotEmpty(message = "이메일 입력은 필수 사항입니다.")
    private String email;
}
