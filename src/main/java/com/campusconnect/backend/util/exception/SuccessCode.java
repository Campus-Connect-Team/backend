package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    INSERT_SUCCESS(HttpStatus.CREATED, "201", "생성 성공"),
    READ_SUCCESS(HttpStatus.OK, "200", "조회 성공"),
    UPDATE_SUCCESS(HttpStatus.NO_CONTENT, "204", "수정 성공"),
    DELETE_SUCCESS(HttpStatus.NO_CONTENT, "204", "삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;
}
