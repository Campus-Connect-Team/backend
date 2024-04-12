package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 회원가입
    ALREADY_EXISTS_STUDENT_NUMBER(HttpStatus.BAD_REQUEST, "USER-001", "이미 존재하는 학번입니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER-002", "이미 존재하는 이메일입니다."),
    INVALID_AUTHENTICATION_CODE(HttpStatus.BAD_REQUEST, "USER-003", "인증번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER-004", "비밀번호 형식이 일치하지 않습니다."),
    FAILED_MESSAGE_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "USER-005", "인증 코드 메일 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;
}
