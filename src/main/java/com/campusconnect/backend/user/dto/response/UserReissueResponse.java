package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserReissueResponse {

    private String studentNumber;
    private String accessToken;
    private String refreshToken;
    private String responseCode;

    @Builder
    public UserReissueResponse(String studentNumber,
                               String accessToken,
                               String refreshToken,
                               String responseCode) {
        this.studentNumber = studentNumber;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.responseCode = responseCode;
    }
}
