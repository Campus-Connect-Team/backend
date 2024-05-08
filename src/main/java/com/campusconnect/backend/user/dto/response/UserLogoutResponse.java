package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserLogoutResponse {

    private String studentNumber;
    private String responseCode;

    @Builder
    public UserLogoutResponse(String studentNumber, String responseCode) {
        this.studentNumber = studentNumber;
        this.responseCode = responseCode;
    }
}
