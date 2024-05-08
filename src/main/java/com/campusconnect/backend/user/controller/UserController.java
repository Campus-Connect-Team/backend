package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.domain.UserImageInitializer;
import com.campusconnect.backend.user.dto.request.*;
import com.campusconnect.backend.user.dto.response.*;
import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.email.service.EmailService;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import com.campusconnect.backend.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private final JwtProvider jwtProvider;
    private final S3Uploader s3Uploader;

    @Value("${jwt.secret-key}")
    private String secretKey;

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
        userService.login(userLoginRequest);
        return ResponseEntity.status(ErrorCode.SUCCESS_LOGIN.getHttpStatus().value())
                .body(userService.login(userLoginRequest));
    }

    /** 로그아웃 처리 */
    @PostMapping("/users/log-out")
    public ResponseEntity<UserLogoutResponse> logout(HttpServletRequest request, 
                                                     @RequestHeader("Authorization") String accessToken,
                                                     @RequestHeader("RefreshToken") String refreshToken) {
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.logout(accessToken, refreshToken, studentNumber));
    }

    /** Access Token 만료 시, Refresh Token을 통해 Access Token 재발급 */
    @PostMapping("/users/log-in/reissue")
    public ResponseEntity<UserReissueResponse> reissue(@RequestHeader("Authorization") String accessToken, @RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.reissue(accessToken, refreshToken));
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

    /** 마이 페이지 조회 */
    @GetMapping("/users/my-page")
    public ResponseEntity<UserMyProfileAllResponse> getMyProfile(@RequestParam String studentNumber) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getMyProfile(studentNumber));
    }

    /** 마이 페이지 - 기본 프로필 영역 수정 */
    @PatchMapping(value = "/users/my-page/basic/{studentNumber}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserBasicProfileEditResponse> editMyBasicProfile(HttpServletRequest request,
                                                                           @PathVariable(value = "studentNumber") String studentNumber,
                                                                           @RequestPart(value = "request") UserBasicProfileEditRequest userBasicProfileEditRequest,
                                                                           @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateMyBasicProfile(studentNumber, userBasicProfileEditRequest, multipartFile));
    }

    /** 마이 페이지 - 비밀번호 수정 */
    @PatchMapping("/users/my-page/password/{studentNumber}")
    public ResponseEntity<UserPasswordUpdateResponse> updateUserPassword(@PathVariable String studentNumber, @RequestBody @Valid UserPasswordUpdateRequest userPasswordUpdateRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUserPassword(studentNumber, userPasswordUpdateRequest));
    }

    /** 마이 페이지 - 회원 탈퇴 */
    @DeleteMapping("/users/my-page/withdrawal/{studentNumber}")
    public ResponseEntity<UserWithdrawalResponse> withdrawalAccount(@PathVariable String studentNumber, @RequestBody @Valid UserWithdrawalRequest userWithdrawalRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.withdrawalAccount(studentNumber, userWithdrawalRequest));
    }
}
