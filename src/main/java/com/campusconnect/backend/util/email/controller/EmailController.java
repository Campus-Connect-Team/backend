package com.campusconnect.backend.util.email.controller;

import com.campusconnect.backend.util.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;
}
