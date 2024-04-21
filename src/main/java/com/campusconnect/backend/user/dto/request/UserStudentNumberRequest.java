package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserStudentNumberRequest {

    @NotEmpty(message = "학번 입력은 필수 사항입니다.")
    @Size(min = 8, max = 8, message = "학번은 최대 8글자로 입력해야 합니다.")
    private String studentNumber;
}
