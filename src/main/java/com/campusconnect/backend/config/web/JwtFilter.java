package com.campusconnect.backend.config.web;

import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.exception.ErrorCode;
import com.campusconnect.backend.util.exception.ErrorResponse;
import com.campusconnect.backend.util.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Token이 존재하지 않는 경우 권한을 Block한다.
            final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("authorization = {}", authorization);

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.error("authorization이 존재하지 않습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // Token을 꺼낸다.
            String token = authorization.split(" ")[1];

            // Token이 만료되었는지 검증한다.
            if (jwtProvider.isExpired(token, secretKey)) {
                log.error("Token이 만료되었습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 학번 정보를 Token에서 가져온다.
            String studentNumber = "";
            studentNumber = jwtProvider.getStudentNumber(token, secretKey);
            log.info("학번 정보(studentNumber) = {}", studentNumber);

            // 권한 부여
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(studentNumber, null, List.of(new SimpleGrantedAuthority("USER")));

            // Detail 부여
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException exception) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.EXPIRED_TOKEN);
            sendErrorRelatedToken(response, errorResponse);
        } catch (MalformedJwtException exception) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.JWT_SIGNATURE_FAIL);
            sendErrorRelatedToken(response, errorResponse);
        }

    }

    private void sendErrorRelatedToken(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorReponse = objectMapper.writeValueAsString(errorResponse);

        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonErrorReponse);
    }
}
