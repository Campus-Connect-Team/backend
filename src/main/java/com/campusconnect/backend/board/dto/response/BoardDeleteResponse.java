package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BoardDeleteResponse {

    private Long boardId;
    private String title;
    private String errorCode;

    @Builder
    public BoardDeleteResponse(Long boardId, String title, String errorCode) {
        this.boardId = boardId;
        this.title = title;
        this.errorCode = errorCode;
    }
}
