package com.campusconnect.backend.util.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public enum ResponseStatusCode {

    // 회원가입 관련 상태 응답
    AVAILABLE_STUDENT_NUMBER(HttpStatus.OK, "USER-STATUS-001", "가입 가능한 학번입니다."),

    // 데이터베이스 오류
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE-STATUS-001", "데이터베이스 오류"),

    // 검증 실패
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION-STATUS-001", "검증 실패");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

    ResponseStatusCode(HttpStatus httpStatus, String code, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.description = description;
    }
}
