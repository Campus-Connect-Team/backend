package com.campusconnect.backend.comment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class CommentDeleteResponse {

    private Long commentId;
    private String responseCode;

    @Builder
    public CommentDeleteResponse(Long commentId, String responseCode) {
        this.commentId = commentId;
        this.responseCode = responseCode;
    }
}
