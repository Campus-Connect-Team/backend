package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.dto.request.EmailAuthenticationRequest;
import com.campusconnect.backend.user.dto.request.UserEmailRequest;
import com.campusconnect.backend.user.dto.request.UserSignUpRequest;
import com.campusconnect.backend.user.dto.request.UserStudentNumberRequest;
import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/users/sign-up")
    public User createUser(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        return userService.createUser(userSignUpRequest);
    }

    /** 인증코드 발송 */
    @PostMapping("/users/sign-up/email-authentication")
    public void authenticateEmail(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        emailService.authenticateEmail(userEmailRequest);
    }

    @GetMapping("/users/sign-up/studentNumber-duplicate-validation")
    public void validateDuplicateStudentNumber(@RequestBody @Valid UserStudentNumberRequest userStudentNumberRequest) {
        userService.validateDuplicateStudentNumber(userStudentNumberRequest.getStudentNumber());
    }

    @GetMapping("/users/sign-up/email-duplicate-validation")
    public void validateDuplicateEmail(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        userService.validateDuplicateEmail(userEmailRequest.getEmail());
    }

    /** 인증코드 검증 */
    @GetMapping("/users/sign-up/email-authentication")
    public void validateAuthenticationNumber(@RequestBody @Valid EmailAuthenticationRequest emailAuthenticationRequest) {
        userService.validateAuthenticationCode(emailAuthenticationRequest);
    }
}
