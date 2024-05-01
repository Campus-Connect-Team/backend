package com.campusconnect.backend.user.dto.request;

import com.campusconnect.backend.util.validator.PasswordMatches;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class UserPasswordUpdateRequest {

    @NotEmpty(message = "현재 비밀번호 입력은 필수 사항입니다.")
    private String currentPassword;

    @NotEmpty(message = "변경할 비밀번호 입력은 필수 사항입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,20}$",
            message = "비밀번호는 8~20글자 이내로 영문 소문자, 숫자, 특수문자(@#$%^&+=!)를 최소 1개 이상 포함해야 합니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자리 이상 최대 20자리 이하까지 입력해야 합니다.")
    private String editPassword;

    @NotEmpty(message = "변경할 비밀번호를 다시 입력해야 합니다.")
    private String checkEditPassword;
}
