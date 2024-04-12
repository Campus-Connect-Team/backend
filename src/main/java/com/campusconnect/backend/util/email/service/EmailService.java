package com.campusconnect.backend.util.email.service;

import com.campusconnect.backend.authentication.domain.Authentication;
import com.campusconnect.backend.authentication.repository.AuthenticationRepository;
import com.campusconnect.backend.user.dto.request.UserEmailRequest;
import com.campusconnect.backend.user.repository.UserRepository;
import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;

    private final String SUBJECT = "Campus Connect 회원가입을 위한 인증 코드 메일입니다.";

    public boolean sendCertificationMail(String email, String certificationNumber) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void authenticateEmail(UserEmailRequest userEmailRequest) {
        String email = userEmailRequest.getEmail();
        email += "@sungkyul.ac.kr";

        String authenticationNumber = createCode();

        if (!sendCertificationMail(email, authenticationNumber)) {
            throw new CustomException(ErrorCode.FAILED_MESSAGE_SEND);
        }
        
        // 보낸 인증 코드를 인증 테이블에 저장한다.
        Authentication savedAuthenticationInfo = new Authentication(email, authenticationNumber);
        authenticationRepository.save(savedAuthenticationInfo);
    }

    private String getCertificationMessage(String certificationNumber) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center>Campus Connect 회원가입을 위한 인증 코드 메일입니다.</h1>";
        certificationMessage += "<h3 style='text-align: center>인증코드 <strong style='font-size: 32px; letter-spacing: 8px;'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;
    }

    private String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 95)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    // Thymeleaf 적용
    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}
