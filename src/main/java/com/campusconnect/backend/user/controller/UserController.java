package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserImageInitializer;
import com.campusconnect.backend.user.dto.request.*;
import com.campusconnect.backend.user.dto.response.UserLoginResponse;
import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.email.service.EmailService;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final S3Uploader s3Uploader;

    @PostMapping(value = "/users/sign-up", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public User createUser(HttpServletRequest request,
                           @RequestPart(value = "request") @Valid UserSignUpRequest userSignUpRequest,
                           @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        // 이미지 업로드 및 URL 생성
        String imageUrl;
        if (multipartFile != null) {
            imageUrl = s3Uploader.upload(multipartFile, "user");
        } else {
            imageUrl = UserImageInitializer.getDefaultImageUrl();
        }
        userSignUpRequest.setImage(imageUrl);

        return userService.createUser(userSignUpRequest, multipartFile);
    }

    /** 인증코드 발송 */
    @PostMapping("/users/sign-up/email-authentication")
    public void authenticateEmail(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        emailService.authenticateEmail(userEmailRequest);
    }

    /** 로그인 처리 */
    @PostMapping("/users/log-in")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        userService.userLogin(userLoginRequest);
        return ResponseEntity.status(ErrorCode.SUCCESS_LOGIN.getHttpStatus().value())
                .body(userService.userLogin(userLoginRequest));
    }

    @GetMapping("/users/sign-up/studentNumber-duplicate-validation")
    public ResponseEntity<ErrorResponse> validateDuplicateStudentNumber(@RequestBody @Valid UserStudentNumberRequest userStudentNumberRequest) {
        userService.validateDuplicateStudentNumber(userStudentNumberRequest.getStudentNumber());
        return ResponseEntity.status(ErrorCode.AVAILABLE_STUDENT_NUMBER.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.AVAILABLE_STUDENT_NUMBER));
    }

    @GetMapping("/users/sign-up/email-duplicate-validation")
    public ResponseEntity<ErrorResponse> validateDuplicateEmail(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        userService.validateDuplicateEmail(userEmailRequest.getEmail());
        return ResponseEntity.status(ErrorCode.AVAILABLE_EMAIL.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.AVAILABLE_EMAIL));
    }

    /** 인증코드 검증 */
    @GetMapping("/users/sign-up/email-authentication")
    public ResponseEntity<ErrorResponse> validateAuthenticationNumber(@RequestBody @Valid EmailAuthenticationRequest emailAuthenticationRequest) {
        userService.validateAuthenticationCode(emailAuthenticationRequest);
        return ResponseEntity.status(ErrorCode.MATCH_AUTHENTICATION_CODE.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.MATCH_AUTHENTICATION_CODE));
    }
}
