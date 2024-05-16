package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserSignUpRequest {

    private String image;

    @NotEmpty(message = "이름 입력은 필수 사항입니다.")
    @Size(max = 5, message = "이름은 최대 5글자까지 입력할 수 있습니다.")
    private String name;

    @NotEmpty(message = "학번 입력은 필수 사항입니다.")
    @Pattern(regexp = "[0-9]+", message = "학번은 0~9까지 숫자로만 입력할 수 있습니다.")
    @Size(min = 8, max = 8, message = "학번은 8자리로 입력해야 합니다.")
    private String studentNumber;

    @NotEmpty(message = "이메일 입력은 필수 사항입니다.")
    @Size(max = 50, message = "\"@sungkyul.ac.kr\"을 제외한 이메일은 최대 35글자까지 입력할 수 있습니다.")
    private String email;

    @NotEmpty(message = "인증코드 입력은 필수 사항입니다.")
    private String authenticationNumber;

    @NotEmpty(message = "비밀번호 입력은 필수 사항입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,20}$",
            message = "비밀번호는 8~20글자 이내로 영문 소문자, 숫자, 특수문자(@#$%^&+=!)를 최소 1개 이상 포함해야 합니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자리 이상 최대 20자리 이하까지 입력해야 합니다.")
    private String password;

    @NotEmpty(message = "단과대학 정보는 필수 사항입니다.")
    private String college;

    @NotEmpty(message = "학과(학부) 정보는 필수 사항입니다.")
    private String department;

    @Builder
    public UserSignUpRequest(String name,
                             String studentNumber,
                             String email,
                             String password,
                             String college,
                             String department) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.password = password;
        this.college = college;
        this.department = department;
    }
}
