package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.dto.request.UserSignUpRequest;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test2", "aws"})
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void deleteDB() {
        userRepository.deleteAll();
    }

    @Test
    @Rollback(value = false)
    public void 회원_가입_테스트() throws Exception {
        // Given
        UserSignUpRequest request = new UserSignUpRequest();
        request.setImage("");
        request.setName("유원준");
        request.setStudentNumber("20170931");
        request.setEmail("messonetne");
        request.setPassword("abc1234!@#");
        request.setCollege("IT공과대학");
        request.setDepartment("정보통신공학과");

        // When
        User savedUser = userService.createUser(request, multipartFile);

        // Then
        Assertions.assertThat("https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/BasicProfileImage.png").isEqualTo(savedUser.getImage());
        Assertions.assertThat("유원준").isEqualTo(savedUser.getName());
        Assertions.assertThat("20170931").isEqualTo(savedUser.getStudentNumber());
        Assertions.assertThat("messonetne@sungkyul.ac.kr").isEqualTo(savedUser.getEmail());
        Assertions.assertThat("IT공과대학").isEqualTo(savedUser.getCollege());
        Assertions.assertThat("정보통신공학과").isEqualTo(savedUser.getDepartment());
    }

}