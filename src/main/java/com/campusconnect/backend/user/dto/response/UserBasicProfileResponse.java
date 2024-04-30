package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserBasicProfileResponse {

    private String college;
    private String department;
    private String studentNumber;
    private String name;
    private String email;
    private String image;

    @Builder
    public UserBasicProfileResponse(String college,
                                    String department,
                                    String studentNumber,
                                    String name,
                                    String email,
                                    String image) {
        this.college = college;
        this.department = department;
        this.studentNumber = studentNumber;
        this.name = name;
        this.email = email;
        this.image = image;
    }
}
