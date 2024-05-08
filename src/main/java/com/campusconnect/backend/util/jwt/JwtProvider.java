package com.campusconnect.backend.util.jwt;

import com.campusconnect.backend.config.redis.RedisConfig;
import com.campusconnect.backend.config.redis.RedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final RedisConfig redisConfig;
    private final RedisRepository redisRepository;
    private static final Long accessTokenExpiredMs = 1000 * 60 * 30L;  // 30 Minutes
    private static final Long refreshTokenExpiredMs = 1000 * 60L;  // 1 Days

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

        return token;
    }

    public String createAccessToken(String studentNumber, String secretKey, Long accessTokenExpiredMs) {
        return this.createToken(studentNumber, secretKey, accessTokenExpiredMs);
    }

    public String createRefreshToken(String studentNumber, String secretKey, Long refreshTokenExpiredMs) {
        return this.createToken(studentNumber, secretKey, refreshTokenExpiredMs);
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

    /**  Header에서 AccessToken 값을 가져온다. */
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /** Access Token의 남은 유효시간을 계산 */
    public Long getAccessTokenExpiredMs(String accessToken) {
        Date expirationMs = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();

        long nowMs = new Date().getTime();
        return expirationMs.getTime() - nowMs;
    }


    /** Refresh Token의 남은 유효시간을 계산 */
    public Long getRefreshTokenExpiredMs(String refreshToken) {
        Date expirationMs = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getExpiration();

        long nowMs = new Date().getTime();
        return expirationMs.getTime() - nowMs;
    }

    /** Refresh Token의 만료기간 검증 */
    public boolean isRefreshTokenExpired(String refreshToken) {
        Long remainingMs = getRefreshTokenExpiredMs(refreshToken);
        return remainingMs <= 0;
    }


    /**
     * 토큰 예외 중 만료 상황만 검증 함수
     */
    public boolean validateTokenExceptExpiration(String token) {
        final String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        try {
            Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
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
