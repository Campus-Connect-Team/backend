package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserLoginResponse {

    private String studentNumber;
    private String responseCode;
    private String accessToken;
    private String refreshToken;

    @Builder
    public UserLoginResponse(String studentNumber, String responseCode, String accessToken, String refreshToken) {
        this.studentNumber = studentNumber;
        this.responseCode = responseCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
