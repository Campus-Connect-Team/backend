package com.campusconnect.backend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 회원 도메인 : 회원 가입 (사용자 인증 처리 관련 포함)
    ALREADY_EXISTS_STUDENT_NUMBER(HttpStatus.CONFLICT, "USER-001", "이미 존재하는 학번입니다."),
    INVALID_STUDENT_NUMBER(HttpStatus.BAD_REQUEST, "USER-002", "가입 가능한 학번은 8자리입니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER-003", "이미 존재하는 이메일입니다."),
    INVALID_AUTHENTICATION_CODE(HttpStatus.BAD_REQUEST, "USER-004", "인증번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER-005", "비밀번호 형식이 일치하지 않습니다."),
    FAILED_MESSAGE_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "USER-006", "인증 코드 메일 전송에 실패했습니다."),
    NOT_FOUND_PROFILE_IMAGE(HttpStatus.NOT_FOUND, "USER-007", "프로필 사진을 찾을 수 없습니다."),

    UNAUTHORIZED_CLIENT(HttpStatus.UNAUTHORIZED,"AUTHORIZATION-001", "접근 토큰이 없습니다."),
    FORBIDDEN_CLIENT(HttpStatus.FORBIDDEN, "AUTHORIZATION-002","접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-003","만료된 토큰입니다."),
    EXPIRED_OR_NOT_EXISTED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-004","Refresh Token이 존재하지 않거나 만료되어 클라이언트의 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-005","클라이언트로부터 넘겨받은 Refresh Token이 유효하지 않습니다."),
    JWT_DECODE_FAIL(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-006","올바른 토큰이 필요합니다."),
    JWT_SIGNATURE_FAIL(HttpStatus.UNAUTHORIZED, "AUTHORIZATION-007","올바른 토큰이 필요합니다."),


    // 회원 도메인 : 회원가입 - 처리 가능한 요청에 대한 Response Status Code
    AVAILABLE_STUDENT_NUMBER(HttpStatus.OK, "USER-008", "가입 가능한 학번입니다."),
    AVAILABLE_EMAIL(HttpStatus.OK, "USER-009", "가입 가능한 이메일입니다."),
    SUCCESS_SEND_AUTHENTICATION_MAIL(HttpStatus.OK, "USER-010", "인증 코드 이메일이 발송되었습니다."),
    MATCH_AUTHENTICATION_CODE(HttpStatus.OK, "USER-011", "인증코드가 일치합니다."),
    SUCCESS_REISSUE_ACCESS_TOKEN(HttpStatus.OK, "USER-012", "새로운 Access Token이 발급되었습니다."),
    SUCCESS_TEMPORAL_PASSWORD(HttpStatus.OK, "USER-013", "새로운 임시 비밀번호가 해당 메일로 전송되었습니다."),
    SUCCESS_SIGN_UP(HttpStatus.OK, "USER-014", "회원가입이 완료되었습니다."),


    // 회원 도메인 : 로그인, 로그아웃
    FAIL_LOGIN(HttpStatus.UNAUTHORIZED, "USER-015", "로그인에 실패했습니다. 학번 또는 비밀번호가 정확한지 확인해 주세요."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-016",  "해당 학번으로 회원 정보가 존재하지 않습니다."),
    NOT_PUBLISH_TOKEN(HttpStatus.UNAUTHORIZED, "USER-017", "토큰이 발행되지 않았습니다."),
    LOGGED_OUT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "USER-018", "이미 로그아웃 처리된 Access Token입니다."),
    NOT_EXISTED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "USER-019", "해당 사용자(학번)로 Refresh Token이 존재하지 않습니다"),
    NOT_EXISTED_ACCESS_GRANT(HttpStatus.UNAUTHORIZED, "USER-020", "로그아웃하셨거나, 로그인하지 않아 서비스 접근 권한이 없습니다."),



    // 회원 도메인 : 로그인, 로그아웃  - 처리 가능한 요청에 대한 Response Status Code
    SUCCESS_LOGIN(HttpStatus.OK, "USER-021", "서비스에 로그인되었습니다."),
    SUCCESS_TOKEN_REISSUE(HttpStatus.OK, "USER-022", "토큰이 재발급되었습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "USER-023", "서비스에서 로그아웃되었습니다."),



    // 회원 도메인 : 마이 페이지
    NOT_MATCHED_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "MY_PAGE-001", "현재 비밀번호가 일치하지 않습니다."),
    NOT_MATCHED_CHECK_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "MY_PAGE-002", "입력하신 현재 비밀번호와 일치하지 않습니다."),
    NOT_MATCHED_EDIT_PASSWORD(HttpStatus.BAD_REQUEST, "MY_PAGE-003", "변경할 비밀번호가 일치하지 않습니다."),


    // 회원 도메인 : 마이 페이지 - 처리 가능한 요청에 대한 Response Status Code
    SUCCESS_EDIT_MY_BASIC_PROFILE(HttpStatus.OK, "MY_PAGE-004", "기본 프로필 수정이 완료되었습니다."),
    SUCCESS_UPDATE_PASSWORD(HttpStatus.OK, "MY_PAGE-005", "비밀번호가 변경되었습니다."),
    SUCCESS_WITHDRAWAL_USER(HttpStatus.OK, "MY_PAGE-006", "회원 탈퇴가 완료되었습니다."),




    // 게시판 도메인 : 게시글 기본 CRUD, 그외 세부 기능들
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "BOARD-001", "해당 판매 게시글이 존재하지 않습니다."),
    NOT_FOUND_BOARD_IMAGES(HttpStatus.BAD_REQUEST, "BOARD-002", "판매 게시글에 대한 판매 사진이 업로드되지 않았습니다."),
    EXCEEDED_LIMIT_BOARD_IMAGES(HttpStatus.BAD_REQUEST, "BOARD-003", "판매 게시글 사진은 최대 10장까지만 업로드할 수 있습니다."),
    CANNOT_UPDATE_BOARD_BECAUSE_TRADE_COMPLETION(HttpStatus.BAD_REQUEST, "BOARD-004", "거래완료로 처리된 판매 게시글은 수정할 수 없습니다."),
    CANNOT_UPDATE_OR_DELETE_BOARD_BECAUSE_NOT_RELEVANT_USER(HttpStatus.BAD_REQUEST, "BOARD-005", "본인이 작성하지 않은 게시글은 수정 또는 삭제할 수 없습니다."),
    CANNOT_DELETE_BOARD(HttpStatus.BAD_REQUEST, "BOARD-006", "거래완료로 처리된 판매 게시글은 삭제할 수 없습니다."),
    CANNOT_INCREASE_BOARD_FAVORITE_COUNT(HttpStatus.BAD_REQUEST, "BOARD-007", "이미 관심 게시글(상품)로 등록하셨습니다."),
    LIMIT_EXCEEDED_BY_ZERO_DECREASE_FAVORITE_COUNT(HttpStatus.BAD_REQUEST, "BOARD-008", "관심 수는 0 미만으로 감소할 수 없습니다."),


    // 게시판 도메인 : 게시글 기본 CRUD, 그외 세부 기능들 - 처리 가능한 요청에 대한 Response Status Code
    NOT_EXISTS_SEARCH_BOARD_RESULTS(HttpStatus.NOT_FOUND, "BOARD-009", "게시글 검색 결과가 존재하지 않습니다."),
    SUCCESS_BOARD_CREATION(HttpStatus.OK, "BOARD-010", "판매 게시글 작성이 완료되었습니다."),
    SUCCESS_BOARD_UPDATE(HttpStatus.OK, "BOARD-011", "판매 게시글 수정이 완료되었습니다."),
    SUCCESS_BOARD_DELETE(HttpStatus.OK, "BOARD-012", "판매 게시글 삭제가 완료되었습니다."),
    SUCCESS_REGISTER_FAVORITE_BOARD(HttpStatus.OK, "BOARD-013", "관심 게시글(상품)로 등록되었습니다."),
    SUCCESS_CANCEL_FAVORITE_BOARD(HttpStatus.OK, "BOARD-014", "관심 게시글(상품)에서 해제되었습니다."),



    // 댓글, 답글 도메인 : 댓글, 답글 기본 CRUD, 그외 세부 기능들
    CANNOT_UPDATE_OR_DELETE_COMMENT_BECAUSE_NOT_RELEVANT_USER(HttpStatus.BAD_REQUEST, "COMMENTS-001", "본인이 작성하지 않은 댓글은 수정 또는 삭제할 수 없습니다."),
    AFFECTED_REPLIES_COUNT_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "COMMENTS-002", "삭제된 답글 수는 음수가 될 수 없습니다."),

    CANNOT_UPDATE_OR_DELETE_REPLY_BECAUSE_NOT_RELEVANT_USER(HttpStatus.BAD_REQUEST, "COMMENTS-003", "본인이 작성하지 않은 답글은 수정 또는 삭제할 수 없습니다."),


    // 댓글, 답글 도메인 : 댓글, 답글 CRUD, 그외 세부 기능들 - 처리 가능한 요청에 대한 Response Status Code
    NOT_EXISTS_COMMENTS(HttpStatus.NOT_FOUND, "COMMENTS-004", "게시글에 대한 댓글이 존재하지 않습니다."),
    NOT_EXISTS_REPLIES(HttpStatus.NOT_FOUND, "COMMENTS-005", "댓글에 대한 답글이 존재하지 않습니다."),
    SUCCESS_COMMENT_CREATION(HttpStatus.OK, "COMMENTS-006", "댓글 작성이 완료되었습니다."),
    SUCCESS_COMMENT_UPDATE(HttpStatus.OK, "COMMENTS-007", "댓글 수정이 완료되었습니다."),
    SUCCESS_COMMENT_DELETE(HttpStatus.OK, "COMMENTS-008", "댓글 삭제가 완료되었습니다."),
    SUCCESS_REPLY_CREATION(HttpStatus.OK, "COMMENTS-009", "답글 작성이 완료되었습니다."),
    SUCCESS_REPLY_UPDATE(HttpStatus.OK, "COMMENTS-010", "답글 수정이 완료되었습니다."),
    SUCCESS_REPLY_DELETE(HttpStatus.OK, "COMMENTS-011", "답글 삭제가 완료되었습니다."),




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
