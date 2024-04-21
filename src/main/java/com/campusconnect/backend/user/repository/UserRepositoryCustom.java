package com.campusconnect.backend.user.repository;

import com.campusconnect.backend.user.domain.User;

import java.util.Optional;

public interface UserRepositoryCustom {

    // 회원가입 시 중복된 이메일인지 검증
    Boolean isEmailPrefixDuplicated(String email);

    // 로그인 시 입력한 학번으로 비밀번호가 일치하는지 검증
    User findByEncodedPassword(String studentNumber);
}
