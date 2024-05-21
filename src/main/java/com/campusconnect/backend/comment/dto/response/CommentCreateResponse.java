package com.campusconnect.backend.comment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentCreateResponse {

    private Long boardId;
    private Integer commentCount;
    private Long commentId;
    private String commenterName;
    private String commenterDepartment;
    private String commentContent;
    private LocalDateTime createdAt;
    private String responseCode;

    @Builder
    public CommentCreateResponse(Long boardId,
                                 Integer commentCount,
                                 Long commentId,
                                 String commenterName,
                                 String commenterDepartment,
                                 String commentContent,
                                 LocalDateTime createdAt,
                                 String responseCode) {
        this.boardId = boardId;
        this.commentCount = commentCount;
        this.commentId = commentId;
        this.commenterName = commenterName;
        this.commenterDepartment = commenterDepartment;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.responseCode = responseCode;
    }
}
