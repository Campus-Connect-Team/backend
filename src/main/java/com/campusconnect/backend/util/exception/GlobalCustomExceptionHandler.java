package com.campusconnect.backend.util.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalCustomExceptionHandler {

    /** Custom Definition Exception */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        log.info("handleCustomException : {}", exception.getErrorCode());
        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus().value())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    /** @Valid 유효성 검증에 문제가 있는 경우 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        log.info("handleMethodArgumentNotValidException : {}", exception.getMessage());
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST_ERROR.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.BAD_REQUEST_ERROR));
    }
}
