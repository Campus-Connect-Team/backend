package com.campusconnect.backend.util.email.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class EmailAuthenticationResponse {

    private EmailAuthenticationResponse() {
        super();
    }

    public static ResponseEntity<EmailAuthenticationResponse> success() {
        EmailAuthenticationResponse responseBody = new EmailAuthenticationResponse();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }
}
