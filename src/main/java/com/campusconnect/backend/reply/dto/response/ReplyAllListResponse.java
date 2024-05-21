package com.campusconnect.backend.reply.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ReplyAllListResponse {

    private Long boardId;
    private Long commentId;
    private List<ReplySingleResponse> replySingleResponses;

    @Builder
    public ReplyAllListResponse(Long boardId,
                                Long commentId,
                                List<ReplySingleResponse> replySingleResponses) {
        this.boardId = boardId;
        this.commentId = commentId;
        this.replySingleResponses = replySingleResponses;
    }
}
