package com.campusconnect.backend.authentication.service;

import com.campusconnect.backend.authentication.repository.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
}
