package com.campusconnect.backend.authentication.repository;

import com.campusconnect.backend.authentication.domain.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long>, AuthenticationRepositoryCustom {
}
