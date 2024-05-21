package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BoardFavoriteResponse {

    private Long boardId;
    private Integer favoriteCount;
    private String userName;
    private String studentNumber;
    private String responseCode;

    @Builder
    public BoardFavoriteResponse(Long boardId,
                                 Integer favoriteCount,
                                 String userName,
                                 String studentNumber,
                                 String responseCode) {
        this.boardId = boardId;
        this.favoriteCount = favoriteCount;
        this.userName = userName;
        this.studentNumber = studentNumber;
        this.responseCode = responseCode;
    }
}
