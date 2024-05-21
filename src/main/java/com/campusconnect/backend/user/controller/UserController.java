package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.config.aws.S3Uploader;
import com.campusconnect.backend.user.dto.request.*;
import com.campusconnect.backend.user.dto.response.*;
import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.email.service.EmailService;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import com.campusconnect.backend.util.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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

    /** 회원 가입 */
    @Operation(summary = "사용자 회원가입", description = "서비스 이용을 위해 사용자가 회원가입을 진행한다. ")
    @PostMapping(value = "/users/sign-up", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserSignUpResponse> createUser(HttpServletRequest request,
                           @RequestPart(value = "request") @Valid UserSignUpRequest userSignUpRequest,
                           @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.createUser(userSignUpRequest, multipartFile));
    }

    /** 인증코드 발송 */
    @Operation(summary = "회원가입 - 사용자 인증 이메일 발송", description = "회원가입을 위해 교내 도메인 메일로 인증 코드를 받는 작업을 진행한다. ")
    @PostMapping("/users/sign-up/email-authentication")
    public ResponseEntity<AuthenticationEmailResponse> authenticateEmail(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(emailService.authenticateEmail(userEmailRequest));
    }

    /** 로그인 처리 */
    @Operation(summary = "사용자 로그인", description = "회원가입 시 입력한 학번, 비밀번호로 로그인을 진행한다." +
            "로그인 API를 통해 AccessToken, RefreshToken을 응답 Body에 담아 전송한다.")
    @PostMapping("/users/log-in")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        userService.login(userLoginRequest);
        return ResponseEntity.status(ErrorCode.SUCCESS_LOGIN.getHttpStatus().value())
                .body(userService.login(userLoginRequest));
    }

    /** 로그아웃 처리 */
    @Operation(summary = "사용자 로그아웃", description = "서비스에서 로그아웃을 진행한다" +
            "로그아웃 시 웹 브라우저에 존재하는 AccessToken, RefreshToken을 모두 삭제시킨다. ")
    @PostMapping("/users/log-out")
    public ResponseEntity<UserLogoutResponse> logout(HttpServletRequest request, 
                                                     @RequestHeader("Authorization") String accessToken,
                                                     @RequestHeader("RefreshToken") String refreshToken) {
        String studentNumber = jwtProvider.getStudentNumber(accessToken, secretKey);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.logout(accessToken, refreshToken, studentNumber));
    }

    /** Access Token 만료 시, Refresh Token을 통해 Access Token 재발급 */
    @Operation(summary = " Access Token 만료 시, Refresh Token을 통해 Access Token 재발급", description = "AccessToken이 만료된 경우 비교적 유효시간이 긴 RefreshToken" +
            "을 통해 AccessToken을 재발급받는다. ")
    @PostMapping("/users/log-in/reissue")
    public ResponseEntity<UserReissueResponse> reissue(@RequestHeader("Authorization") String accessToken, @RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.reissue(accessToken, refreshToken));
    }

    /** 로그인 페이지 - 비밀번호 찾기 */
    @Operation(summary = "로그인 페이지 - 사용자 비밀번호 찾기", description = "사용자가 비밀번호를 분실한 경우 해당 API를 통해 임시 비밀번호를 본인의 메일로 발급받을 수 있다.")
    @PostMapping("/users/log-in/find-password")
    public ResponseEntity<UserFindPasswordResponse> findPassword(@RequestBody @Valid UserFindPasswordRequest userFindPasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findPassword(userFindPasswordRequest));
    }

    /** 회원가입 시 학번 중복 검증 */
    @Operation(summary = "회원가입 시 학번이 중복되는지 검증", description = "학번은 각 사용자마다 중복되는 값을 가질 수 없기 때문에 기존 서비스에 가입된" +
            "사용자의 학번과 본인이 가입 시 입력한 학번이 중복되는지 검증한다.")
    @GetMapping("/users/sign-up/studentNumber-duplicate-validation")
    public ResponseEntity<ErrorResponse> validateDuplicateStudentNumber(@RequestParam String studentNumber) {
        userService.validateDuplicateStudentNumber(studentNumber);
        return ResponseEntity.status(ErrorCode.AVAILABLE_STUDENT_NUMBER.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.AVAILABLE_STUDENT_NUMBER));
    }

    /** 회원가입 시 이메일 중복 검증 */
    @Operation(summary = "회원가입 시 교내 이메일이 중복되는지 검증", description = "교내 이메일은 각 사용자마다 중복되는 이메일을 가질 수 없기 때문에 기존 서비스에 가입된" +
            "사용자의 이메일과 본인이 가입 시 입력한 이메일이 중복되는지 검증한다.")
    @GetMapping("/users/sign-up/email-duplicate-validation")
    public ResponseEntity<ErrorResponse> validateDuplicateEmail(@RequestParam String email) {
        userService.validateDuplicateEmail(email);
        return ResponseEntity.status(ErrorCode.AVAILABLE_EMAIL.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.AVAILABLE_EMAIL));
    }

    /** 인증코드 검증 */
    @Operation(summary = "회원가입 시 메일로 받은 인증코드의 유효성 검증", description = "교내 이메일로 받은 인증코드와 사용자의 인증코드와 사용자가 입력한 실제 인증코드가" +
            "일치하는지 유효성 검사를 진행한다.")
    @GetMapping("/users/sign-up/email-authentication")
    public ResponseEntity<ErrorResponse> validateAuthenticationNumber(@RequestParam String email, @RequestParam String authenticationNumber) {
        userService.validateAuthenticationCode(email, authenticationNumber);
        return ResponseEntity.status(ErrorCode.MATCH_AUTHENTICATION_CODE.getHttpStatus().value())
                .body(new ErrorResponse(ErrorCode.MATCH_AUTHENTICATION_CODE));
    }

    /** 마이 페이지 조회 */
    @Operation(summary = "마이 페이지에서 본인의 프로필 정보, 서비스 이용 내역을 조회", description = "본인의 마이 페이지에서 프로필 정보, 서비스 이용내역(관심 상품 리스트, 작성한 게시글, 작성한 댓글에 대한 판매자 게시글)" +
            "을 전체적으로 조회할 수 있다.")
    @GetMapping("/users/my-page")
    public ResponseEntity<UserMyProfileAllResponse> getMyProfile(@RequestParam String studentNumber) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getMyProfile(studentNumber));
    }

    /** 마이 페이지 - 기본 프로필 영역 수정 */
    @Operation(summary = "마이 페이지에서 본인의 프로필 정보를 수정", description = "마이 페이지에서 본인의 프로필 정보(이름, 단과대학, 학과)를 수정할 수 있다.")
    @PatchMapping(value = "/users/my-page/basic/{studentNumber}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserBasicProfileEditResponse> editMyBasicProfile(HttpServletRequest request,
                                                                           @PathVariable(value = "studentNumber") String studentNumber,
                                                                           @RequestPart(value = "request") UserBasicProfileEditRequest userBasicProfileEditRequest,
                                                                           @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateMyBasicProfile(studentNumber, userBasicProfileEditRequest, multipartFile));
    }

    /** 마이 페이지 - 비밀번호 수정 */
    @Operation(summary = "마이 페이지 프로필 정보에서 본인의 현재 비밀번호를 수정", description = "마이 페이지에서 본인의 로그인 비밀번호를 수정할 수 있다." +
            "현재 비밀번호, 변경할 비밀번호, 변경 비밀번호 재확인을 통해 비밀번호를 수정한다.")
    @PatchMapping("/users/my-page/password/{studentNumber}")
    public ResponseEntity<UserPasswordUpdateResponse> updateUserPassword(@PathVariable String studentNumber, @RequestBody @Valid UserPasswordUpdateRequest userPasswordUpdateRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUserPassword(studentNumber, userPasswordUpdateRequest));
    }

    /** 마이 페이지 - 회원 탈퇴 */
    @Operation(summary = "마이 페이지에서 회원 탈퇴를 진행", description = "마이 페이지에서 회원 탈퇴를 원하는 사용자는 본인의 현재 비밀번호, 현재 비밀번호 재확인을 통해 " +
            "회원 탈퇴를 진행할 수 있다.")
    @DeleteMapping("/users/my-page/withdrawal/{studentNumber}")
    public ResponseEntity<UserWithdrawalResponse> withdrawalAccount(@PathVariable String studentNumber, @RequestBody @Valid UserWithdrawalRequest userWithdrawalRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.withdrawalAccount(studentNumber, userWithdrawalRequest));
    }
}
