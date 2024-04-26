package com.campusconnect.backend.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String createToken(String studentNumber, String secretKey, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("studentNumber", studentNumber);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return "Bearer " + token;
    }

    // 토큰에서 학번 정보를 받는다.
    public String getStudentNumber(String token, String secretKey) {
        // secretKey가 null인지 확인
        if (secretKey == null) {
            log.error("secretKey가 null입니다.");
        }

        // secretKey를 Base64 방식으로 인코딩한다.
        byte[] decodeSecretKey = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(decodeSecretKey);

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("studentNumber", String.class);
    }

    // 토큰 만료 여부를 확인한다.
    public boolean isExpired(String token, String secretKey) {
        // 해당 Token이 Expiration이 현재 시간보다 전이면 Token은 만료됨.

        // secretKey가 null인지 확인
        if (secretKey == null) {
            log.info("secretKey = {}", secretKey);
            log.error("secretKey가 null입니다.");
        }

        // secretKey를 Base64 방식으로 인코딩한다.
        byte[] decodeSecretKey = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(decodeSecretKey);

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String validate(String jwt) {
        String subject = null;

        // secretKey를 Base64 방식으로 디코딩한다.
        byte[] decodeSecretKey = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(decodeSecretKey);

        try {
            Claims claims = (Claims) Jwts.parser()
                    .setSigningKey(key)
                    .parse(jwt)
                    .getBody();
            subject = claims.getSubject();

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return subject;
    }
}
