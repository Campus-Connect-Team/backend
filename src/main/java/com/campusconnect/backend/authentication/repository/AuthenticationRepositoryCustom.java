package com.campusconnect.backend.authentication.repository;

public interface AuthenticationRepositoryCustom {

    // 회원가입 시, 인증 코드를 정확히 입력했는지 검증한다.
    Boolean isCorrectAuthenticationNumber(String email, String authenticationNumber);
}
