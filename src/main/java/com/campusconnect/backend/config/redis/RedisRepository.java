package com.campusconnect.backend.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void setRefreshToken(String studentNumber, String refreshToken, long refreshTokenTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(refreshToken.getClass()));
        redisTemplate.opsForValue().set(studentNumber, refreshToken, refreshTokenTime);
    }

    public String getRefreshToken(String studentNumber) {
        return  redisTemplate.opsForValue().get(studentNumber);
    }

    public void deleteRefreshToken(String studentNumber) {
        redisTemplate.delete(studentNumber);
    }

    public boolean hasKey(String studentNumber) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(studentNumber));
    }

    public void setBlackList(String accessToken, String msg, long minutes) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(msg.getClass()));
    }

    public String getBlackList(String studentNumber) {
        return redisTemplate.opsForValue().get(studentNumber);
    }

    public boolean deleteBlackList(String studentNumber) {
        return Boolean.TRUE.equals(redisTemplate.delete(studentNumber));
    }

    // Redis에 존재하는 모든 데이터 삭제
    public void flushAll() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }
}
