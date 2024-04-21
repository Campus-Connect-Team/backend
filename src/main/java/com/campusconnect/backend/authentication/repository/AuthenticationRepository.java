package com.campusconnect.backend.authentication.repository;

import com.campusconnect.backend.authentication.domain.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long>, AuthenticationRepositoryCustom {

    // 회원가입 후 해당 이메일 기준 인증 정보를 모두 지운다.
    void deleteAllByEmail(String email);
}
