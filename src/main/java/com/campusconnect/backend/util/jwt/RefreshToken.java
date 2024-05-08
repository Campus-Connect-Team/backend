package com.campusconnect.backend.util.jwt;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String studentNumber;

    private String refreshToken;

    @TimeToLive
    private Long remainingMilliSeconds;

    @Builder
    public RefreshToken(String studentNumber, String refreshToken, Long remainingMilliSeconds) {
        this.studentNumber = studentNumber;
        this.refreshToken = refreshToken;
        this.remainingMilliSeconds = remainingMilliSeconds / 1000;
    }
}
