package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserBasicProfileEditResponse {

    private Long userId;
    private String name;
    private String college;
    private String department;
    private String responseCode;

    @Builder
    public UserBasicProfileEditResponse(Long userId, String name, String college, String department, String responseCode) {
        this.userId = userId;
        this.name = name;
        this.college = college;
        this.department = department;
        this.responseCode = responseCode;
    }
}
