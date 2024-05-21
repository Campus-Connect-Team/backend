package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserLoginResponse {

    private String studentNumber;
    private String department;
    private String name;
    private String userProfileImage;
    private String responseCode;
    private String accessToken;
    private String refreshToken;

    @Builder
    public UserLoginResponse(String studentNumber,
                             String department,
                             String name,
                             String userProfileImage,
                             String responseCode,
                             String accessToken,
                             String refreshToken) {
        this.studentNumber = studentNumber;
        this.department = department;
        this.name = name;
        this.userProfileImage = userProfileImage;
        this.responseCode = responseCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
