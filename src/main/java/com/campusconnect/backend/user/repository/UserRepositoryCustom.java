package com.campusconnect.backend.user.repository;

public interface UserRepositoryCustom {

    // 회원가입 시 중복된 이메일인지 검증
    Boolean isEmailPrefixDuplicated(String email);
}
