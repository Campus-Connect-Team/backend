package com.campusconnect.backend.util.jwt;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("logoutAccessToken")
public class LogoutAccessToken {

    @Id
    private String studentNumber;

    private String accessToken;

    @TimeToLive
    private Long remainingMilliSeconds;

    @Builder
    public LogoutAccessToken(String studentNumber, String accessToken, Long remainingMilliSeconds) {
        this.studentNumber = studentNumber;
        this.accessToken = accessToken;
        this.remainingMilliSeconds = remainingMilliSeconds / 1000;
    }
}
