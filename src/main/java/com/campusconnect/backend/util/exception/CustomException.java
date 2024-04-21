package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    ErrorCode errorCode;
}
