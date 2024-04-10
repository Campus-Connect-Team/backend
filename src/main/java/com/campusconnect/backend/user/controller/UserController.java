package com.campusconnect.backend.user.controller;

import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.user.dto.request.UserSignUpRequest;
import com.campusconnect.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/users/sign-up")
    public User createUser(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        return userService.createUser(userSignUpRequest);
    }
}
