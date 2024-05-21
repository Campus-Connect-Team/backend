package com.campusconnect.backend.comment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class BoardCommentAllListResponse {

    private Long boardId;
    private Integer commentCount;
    private List<BoardCommentResponse> boardCommentResponses;

    @Builder
    public BoardCommentAllListResponse(Long boardId,
                                       Integer commentCount,
                                       List<BoardCommentResponse> boardCommentResponses) {
        this.boardId = boardId;
        this.commentCount = commentCount;
        this.boardCommentResponses = boardCommentResponses;
    }
}
