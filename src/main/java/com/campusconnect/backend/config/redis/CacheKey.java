package com.campusconnect.backend.config.redis;

import lombok.Getter;

@Getter
public class CacheKey {

    public static final String STUDENT_NUMBER = "student_number";
    public static final int DEFAULT_EXPIRE_SEC = 60;
}
