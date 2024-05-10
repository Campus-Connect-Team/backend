package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BoardCreateResponse {

    private Long boardId;
    private String studentNumber;
    private String title;
    private String responseCode;

    @Builder
    public BoardCreateResponse(Long boardId,
                               String studentNumber,
                               String title,
                               String responseCode) {
        this.boardId = boardId;
        this.studentNumber = studentNumber;
        this.title = title;
        this.responseCode = responseCode;
    }
}
