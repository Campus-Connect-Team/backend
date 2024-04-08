package com.campusconnect.backend.util.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationHandler extends SimpleUrlAuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception
                                        ) throws IOException, ServletException {
        logger.info("Login Fail Handler");

        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "학번 또는 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 이후 로그인해 주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 시스템 관리자에게 문의하십시오.";
        } else {
            errorMessage = "알 수 없는 오류로 인해 로그인에 실패했습니다. 시스템 관리자에게 문의하십시오.";
        }

        URLEncoder.encode(errorMessage, "UTF-8");  //  한글 Encoding conflict 해결
        setDefaultFailureUrl(errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}
