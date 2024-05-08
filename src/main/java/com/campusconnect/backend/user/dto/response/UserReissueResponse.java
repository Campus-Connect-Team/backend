package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserReissueResponse {

    private String accessToken;
    private String refreshToken;
    private String responseCode;

    @Builder
    public UserReissueResponse(String accessToken, String refreshToken, String responseCode) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.responseCode = responseCode;
    }
}
