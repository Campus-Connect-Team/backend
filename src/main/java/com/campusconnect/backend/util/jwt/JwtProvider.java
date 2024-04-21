package com.campusconnect.backend.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
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
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return "Bearer " + token;
    }

    // 토큰에서 학번 정보를 받는다.
    public static String getStudentNumber(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("studentNumber", String.class);
    }

    // 토큰 만료 여부를 확인한다.
    public static boolean isExpired(String token, String secretKey) {
        // 해당 Token이 Expiration이 현재 시간보다 전이면 Token은 만료됨.
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String validate(String jwt) {
        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

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
