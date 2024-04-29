package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardFavoriteResponse {

    private Long boardId;
    private Integer favoriteCount;
    private String userName;
    private String studentNumber;
    private String errorCode;

    @Builder
    public BoardFavoriteResponse(Long boardId,
                                   Integer favoriteCount,
                                   String userName,
                                   String studentNumber,
                                   ErrorCode errorCode) {
        this.boardId = boardId;
        this.favoriteCount = favoriteCount;
        this.userName = userName;
        this.studentNumber = studentNumber;
        this.errorCode = errorCode.getDescription();
    }
}
