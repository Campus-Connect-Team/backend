package com.campusconnect.backend.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserImageInitializer {

    private static final String INIT_URL = "https://campus-connect-backend.s3.ap-northeast-2.amazonaws.com/BasicProfileImage.png";

    public static String getDefaultImageUrl() {
        return INIT_URL;
    }
}
