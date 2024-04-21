package com.campusconnect.backend.user.dto.response;

import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class UserLoginResponse {

    private String studentNumber;
    private String token;
    private Long expirationTime;

    public UserLoginResponse(String token) {
        this.token = token;
        this.expirationTime = 3600L;
    }

    public static ResponseEntity<UserLoginResponse> successLogin(String token) {
        UserLoginResponse responseBody = new UserLoginResponse(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    public static ResponseEntity<ErrorResponse> failLogin() {
        return ResponseEntity.status(ErrorCode.FAIL_LOGIN.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.FAIL_LOGIN));
    }
}
