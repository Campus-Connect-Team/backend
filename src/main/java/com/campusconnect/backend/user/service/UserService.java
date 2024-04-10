package com.campusconnect.backend.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserImageInitializer;
import com.campusconnect.backend.user.domain.UserRole;
import com.campusconnect.backend.user.dto.request.UserSignUpRequest;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket")
    private String bucket;

    @Transactional
    public User createUser(UserSignUpRequest userSignUpRequest) {
        // 학번, 이메일 중복 검증
        checkDuplicationUser(userSignUpRequest.getStudentNumber());
        checkDuplicationEmail(userSignUpRequest.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String image = userSignUpRequest.getImage();
        String name = userSignUpRequest.getName();
        String studentNumber = userSignUpRequest.getStudentNumber();
        String email = userSignUpRequest.getEmail() + "@sungkyul.ac.kr";
        String password = passwordEncoder.encode(userSignUpRequest.getPassword());
        String college = userSignUpRequest.getCollege();;
        String department = userSignUpRequest.getDepartment();

        //  가입 중에 프로필 이미지를 설정하지 않았다면
        if (userSignUpRequest.getImage().isEmpty()) {
            userSignUpRequest.setImage(UserImageInitializer.getDefaultImageUrl());
        }

        User user = User.builder()
                .image(userSignUpRequest.getImage())
                .name(name)
                .studentNumber(studentNumber)
                .email(email)
                .password(password)
                .college(college)
                .department(department)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    /**
     * 중복된 이메일인지 체크한다.
     */
    private void checkDuplicationEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /**
     * 중복된 사용자가 존재하는지 체크한다.
     */
    private void checkDuplicationUser(String studentNumber) {
        if (userRepository.findByStudentNumber(studentNumber).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }
}
