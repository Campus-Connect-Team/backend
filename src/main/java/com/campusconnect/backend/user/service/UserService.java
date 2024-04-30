package com.campusconnect.backend.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.campusconnect.backend.authentication.repository.AuthenticationRepository;
import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.board.repository.BoardRepository;
import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserRole;
import com.campusconnect.backend.user.dto.request.EmailAuthenticationRequest;
import com.campusconnect.backend.user.dto.request.UserLoginRequest;
import com.campusconnect.backend.user.dto.request.UserSignUpRequest;
import com.campusconnect.backend.user.dto.response.UserBasicProfileResponse;
import com.campusconnect.backend.user.dto.response.UserLoginResponse;
import com.campusconnect.backend.user.dto.response.UserMyProfileAllResponse;
import com.campusconnect.backend.user.dto.response.UserSellerAndBuyerScoreResponse;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket")
    private String bucket;

    @Value("${jwt.secret-key}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 10L;

    @Transactional
    public User createUser(UserSignUpRequest userSignUpRequest, MultipartFile multipartFile) throws IOException {
        // 학번, 이메일 중복 검증
        checkDuplicationUser(userSignUpRequest.getStudentNumber());
        checkDuplicationEmail(userSignUpRequest.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String image = userSignUpRequest.getImage();

        String name = userSignUpRequest.getName();
        String studentNumber = userSignUpRequest.getStudentNumber();
        String email = userSignUpRequest.getEmail() + "@sungkyul.ac.kr";
        String password = passwordEncoder.encode(userSignUpRequest.getPassword());
        String college = userSignUpRequest.getCollege();
        String department = userSignUpRequest.getDepartment();

        User user = User.builder()
                .image(image)
                .name(name)
                .studentNumber(studentNumber)
                .email(email)
                .password(password)
                .college(college)
                .department(department)
                .role(UserRole.USER)
                .build();

        authenticationRepository.deleteAllByEmail(email);
        log.info(image);
        return userRepository.save(user);
    }

    /** 중복된 이메일인지 체크한다. */
    public void checkDuplicationEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /** 중복된 사용자가 존재하는지 체크한다. */
    public void checkDuplicationUser(String studentNumber) {
        if (userRepository. findByStudentNumber(studentNumber).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }

    /** 중복된 학번인지 체크한다. */
    public void validateDuplicateStudentNumber(String userStudentNumberRequest) {
        if (userRepository.findByStudentNumber(userStudentNumberRequest).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_STUDENT_NUMBER);
        }
    }

    /** 중복된 이메일인지 체크한다. */
    public void validateDuplicateEmail(String email) {
        if (userRepository.isEmailPrefixDuplicated(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /** 인증 코드 일치 검증 */
    public void validateAuthenticationCode(EmailAuthenticationRequest emailAuthenticationRequest) {
        String email = emailAuthenticationRequest.getEmail();
        String authenticationNumber = emailAuthenticationRequest.getAuthenticationNumber();

        if (!authenticationRepository.isCorrectAuthenticationNumber(email, authenticationNumber)) {
            throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_CODE);
        } else {
            log.info("인증번호가 일치합니다.");
        }
    }

    /** 로그인 처리 */
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        User findUser = userRepository.findByStudentNumber(userLoginRequest.getStudentNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.FAIL_LOGIN));

        String studentNumber = userLoginRequest.getStudentNumber();
        if (!findUser.getStudentNumber().equals(studentNumber)) {
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String password = userLoginRequest.getPassword();
        if (!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String token = jwtProvider.createToken(studentNumber, secretKey, expiredMs);
        return new UserLoginResponse(findUser.getStudentNumber(), token, expiredMs);
    }

    /** 마이 페이지 조회 */
    public UserMyProfileAllResponse getMyProfile(String studentNumber) {
        // 기본 프로필 영역
        User findUser = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        UserBasicProfileResponse basicProfileResponse = UserBasicProfileResponse.builder()
                .college(findUser.getCollege())
                .department(findUser.getDepartment())
                .studentNumber(findUser.getStudentNumber())
                .name(findUser.getName())
                .email(findUser.getEmail())
                .image(findUser.getImage())
                .build();

        List<UserBasicProfileResponse> basicProfileResponses = Collections.singletonList(basicProfileResponse);

        return UserMyProfileAllResponse.builder()
                .basicProfileResponses(basicProfileResponses)
                .build();
    }
}
