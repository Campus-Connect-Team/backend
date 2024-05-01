package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserBasicProfileEditResponse {

    private Long userId;
    private String responseCode;

    @Builder
    public UserBasicProfileEditResponse(Long userId, String responseCode) {
        this.userId = userId;
        this.responseCode = responseCode;
    }
}
