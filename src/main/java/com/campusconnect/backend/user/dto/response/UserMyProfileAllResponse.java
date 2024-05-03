package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserMyProfileAllResponse {

    private List<UserBasicProfileResponse> basicProfileResponses;  // 기본 프로필
    private Integer favoriteListCount;  // 관심 게시글(상품) 총 건수
    private List<MyFavoriteListResponse> myFavoriteListResponses;  // 관심 게시글(상품) 리스트
    private Integer createdBoardCount;  // 작성한 게시글 총 건수
    private List<CreatedBoardListResponse> createdBoardListResponses;  //  작성한 게시글 리스트

    @Builder
    public UserMyProfileAllResponse(List<UserBasicProfileResponse> basicProfileResponses,
                                    Integer favoriteListCount,
                                    List<MyFavoriteListResponse> myFavoriteListResponses,
                                    Integer createdBoardCount,
                                    List<CreatedBoardListResponse> createdBoardListResponses) {
        this.basicProfileResponses = basicProfileResponses;
        this.favoriteListCount= favoriteListCount;
        this.myFavoriteListResponses = myFavoriteListResponses;
        this.createdBoardCount = createdBoardCount;
        this.createdBoardListResponses = createdBoardListResponses;
    }
}

