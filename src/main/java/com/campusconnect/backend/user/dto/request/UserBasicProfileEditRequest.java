package com.campusconnect.backend.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UserBasicProfileEditRequest {

    private String image;

    @NotEmpty(message = "단과대학 정보는 필수 사항입니다.")
    private String college;

    @NotEmpty(message = "학과(학부) 정보는 필수 사항입니다.")
    private String department;

    @NotEmpty(message = "이름 입력은 필수 사항입니다.")
    @Size(max = 5, message = "이름은 최대 5글자까지 입력할 수 있습니다.")
    private String name;

    @Builder
    public UserBasicProfileEditRequest(String image,
                                       String college,
                                       String department,
                                       String name) {
        this.image = image;
        this.college = college;
        this.department = department;
        this.name = name;
    }
}
