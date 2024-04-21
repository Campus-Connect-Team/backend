package com.campusconnect.backend.util.exception;

import lombok.Builder;
import lombok.Data;

@Data
public class ErrorResponse {

    private Integer status;
    private String name;
    private String code;
    private String description;

    @Builder
    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getHttpStatus().value();
        this.name = errorCode.name();
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }
}
