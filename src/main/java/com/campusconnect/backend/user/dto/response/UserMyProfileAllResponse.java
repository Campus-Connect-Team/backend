package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserMyProfileAllResponse {

    private List<UserBasicProfileResponse> basicProfileResponses;
    private Integer favoriteListCount;
    private List<MyFavoriteListResponse> myFavoriteListResponses;

    @Builder
    public UserMyProfileAllResponse(List<UserBasicProfileResponse> basicProfileResponses,
                                    Integer favoriteListCount,
                                    List<MyFavoriteListResponse> myFavoriteListResponses) {
        this.basicProfileResponses = basicProfileResponses;
        this.favoriteListCount= favoriteListCount;
        this.myFavoriteListResponses = myFavoriteListResponses;
    }
}

