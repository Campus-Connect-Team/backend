package com.campusconnect.backend.user.repository;

import com.campusconnect.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByStudentNumber(String studentNumber);
}
