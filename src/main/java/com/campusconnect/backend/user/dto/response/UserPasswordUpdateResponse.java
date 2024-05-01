package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserPasswordUpdateResponse {

    private Long userId;
    private String responseCode;

    @Builder
    public UserPasswordUpdateResponse(Long userId, String responseCode) {
        this.userId = userId;
        this.responseCode = responseCode;
    }
}
