package com.campusconnect.backend.home;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String helloApplication() {
        return "Hello Spring World";
    }
}
