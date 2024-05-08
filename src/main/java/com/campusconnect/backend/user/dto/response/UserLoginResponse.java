package com.campusconnect.backend.user.dto.response;

import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    //    public static ResponseEntity<UserLoginResponse> successLogin(String token) {
//        UserLoginResponse responseBody = new UserLoginResponse(token);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(responseBody);
//    }
//
//    public static ResponseEntity<ErrorResponse> failLogin() {
//        return ResponseEntity.status(ErrorCode.FAIL_LOGIN.getHttpStatus().value())
//                .body(new ErrorResponse(ErrorCode.FAIL_LOGIN));
//    }
}
