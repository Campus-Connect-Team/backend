package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BoardDeleteResponse {

    private Long boardId;
    private String title;
    private String responseCode;

    @Builder
    public BoardDeleteResponse(Long boardId, String title, String responseCode) {
        this.boardId = boardId;
        this.title = title;
        this.responseCode = responseCode;
    }
}
