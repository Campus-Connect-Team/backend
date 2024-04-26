package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 회원 도메인 : 회원 가입 (사용자 인증 처리 관련 포함)
    ALREADY_EXISTS_STUDENT_NUMBER(HttpStatus.BAD_REQUEST, "USER-001", "이미 존재하는 학번입니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER-002", "이미 존재하는 이메일입니다."),
    INVALID_AUTHENTICATION_CODE(HttpStatus.BAD_REQUEST, "USER-003", "인증번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER-004", "비밀번호 형식이 일치하지 않습니다."),
    FAILED_MESSAGE_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "USER-005", "인증 코드 메일 전송에 실패했습니다."),
    NOT_FOUND_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "USER-006", "프로필 사진을 찾을 수 없습니다."),

    UNAUTHORIZED_CLIENT(HttpStatus.BAD_REQUEST,"AUTHORIZATION-001", "접근 토큰이 없습니다."),
    FORBIDDEN_CLIENT(HttpStatus.FORBIDDEN, "AUTHORIZATION-002","접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-003","만료된 토큰입니다."),
    JWT_DECODE_FAIL(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-004","올바른 토큰이 필요합니다."),
    JWT_SIGNATURE_FAIL(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-005","올바른 토큰이 필요합니다."),


    // 회원 도메인 : 회원가입 - 처리 가능한 요청에 대한 Response Status Code
    AVAILABLE_STUDENT_NUMBER(HttpStatus.OK, "USER-006", "가입 가능한 학번입니다."),
    AVAILABLE_EMAIL(HttpStatus.OK, "USER-007", "가입 가능한 이메일입니다."),
    MATCH_AUTHENTICATION_CODE(HttpStatus.OK, "USER-008", "인증코드가 일치합니다."),


    // 회원 도메인 : 로그인, 로그아웃
    FAIL_LOGIN(HttpStatus.UNAUTHORIZED, "USER-009", "로그인에 실패했습니다. 학번 또는 비밀번호가 정확한지 확인해 주세요."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-010",  "해당 학번으로 회원 정보가 존재하지 않습니다."),
    NOT_PUBLISH_TOKEN(HttpStatus.BAD_REQUEST, "USER-011", "토큰이 발행되지 않았습니다."),


    // 회원 도메인 : 로그인, 로그아웃  - 처리 가능한 요청에 대한 Response Status Code
    SUCCESS_LOGIN(HttpStatus.OK, "USER-011", "로그인 처리되었습니다."),



    // 게시판 도메인 : 게시글 기본 CRUD, 그외 세부 기능들
    NOT_FOUND_BOARD(HttpStatus.BAD_REQUEST, "BOARD-001", "해당 판매 게시글이 존재하지 않습니다."),
    NOT_FOUND_BOARD_IMAGES(HttpStatus.BAD_REQUEST, "BOARD-002", "판매 게시글에 대한 판매 사진이 업로드되지 않았습니다."),
    EXCEEDED_LIMIT_BOARD_IMAGES(HttpStatus.BAD_REQUEST, "BOARD-003", "판매 게시글 사진은 최대 10장까지만 업로드할 수 있습니다."),
    CANNOT_UPDATE_BOARD(HttpStatus.BAD_REQUEST, "BOARD-004", "거래완료로 처리된 판매 게시글은 수정할 수 없습니다."),
    CANNOT_DELETE_BOARD(HttpStatus.BAD_REQUEST, "BOARD-005", "거래완료로 처리된 판매 게시글은 삭제할 수 없습니다."),


    // 게시판 도메인 : 게시글 기본 CRUD, 그외 세부 기능들 - 처리 가능한 요청에 대한 Response Status Code
    SUCCESS_BOARD_CREATION(HttpStatus.OK, "BOARD-006", "판매 게시글 작성이 완료되었습니다."),
    SUCCESS_BOARD_UPDATE(HttpStatus.OK, "BOARD-007", "판매 게시글 수정이 완료되었습니다."),
    SUCCESS_BOARD_DELETE(HttpStatus.OK, "BOARD-008", "판매 게시글 수정이 완료되었습니다."),




    /**
     * *************** Global Error CodeList *****************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *******************************************************
     */
    // 잘못된 클라이언트의 요청
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "클라이언트의 요청 정보 유효성이 올바르지 않습니다."),

    // @RequestBody 데이터 미존재
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(HttpStatus.BAD_REQUEST, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "G006", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "G007", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G008", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G009", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(HttpStatus.NOT_FOUND, "G010", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(HttpStatus.NOT_FOUND, "G011", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "G012", "Header에 데이터가 존재하지 않는 경우 "),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "Internal Server Error Exception");

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;
}
