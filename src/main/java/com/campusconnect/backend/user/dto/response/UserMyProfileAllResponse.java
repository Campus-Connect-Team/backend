package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserMyProfileAllResponse {

    private List<UserBasicProfileResponse> basicProfileResponses;

    @Builder
    public UserMyProfileAllResponse(List<UserBasicProfileResponse> basicProfileResponses) {
        this.basicProfileResponses = basicProfileResponses;
    }
}

