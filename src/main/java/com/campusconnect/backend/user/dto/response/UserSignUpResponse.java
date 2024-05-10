package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserSignUpResponse {

    private String studentNumber;
    private String college;
    private String department;
    private String name;
    private String responseCode;

    @Builder
    public UserSignUpResponse(String studentNumber, String college, String department, String name, String responseCode) {
        this.studentNumber = studentNumber;
        this.college = college;
        this.department = department;
        this.name = name;
        this.responseCode = responseCode;
    }
}
