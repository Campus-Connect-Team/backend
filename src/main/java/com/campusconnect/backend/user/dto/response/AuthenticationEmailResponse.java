package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthenticationEmailResponse {

    private String email;
    private String responseCode;

    @Builder
    public AuthenticationEmailResponse(String email, String responseCode) {
        this.email = email;
        this.responseCode = responseCode;
    }
}
