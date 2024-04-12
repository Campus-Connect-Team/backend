package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private Integer status;
    private String name;
    private String code;
    private String description;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .name(errorCode.name())
                        .code(errorCode.getCode())
                        .description(errorCode.getDescription())
                        .build());
    }
}
