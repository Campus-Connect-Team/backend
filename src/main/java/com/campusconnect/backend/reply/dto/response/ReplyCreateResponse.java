package com.campusconnect.backend.reply.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyCreateResponse {

    private Long boardId;
    private Long commentId;
    private Long replyId;
    private String replierName;
    private String replierDepartment;
    private String replyContent;
    private LocalDateTime createdAt;
    private String responseCode;

    @Builder
    public ReplyCreateResponse(Long boardId,
                               Long commentId,
                               Long replyId,
                               String replierName,
                               String replierDepartment,
                               String replyContent,
                               LocalDateTime createdAt,
                               String responseCode) {
        this.boardId = boardId;
        this.commentId = commentId;
        this.replyId = replyId;
        this.replierName = replierName;
        this.replierDepartment = replierDepartment;
        this.replyContent = replyContent;
        this.createdAt = createdAt;
        this.responseCode = responseCode;
    }
}
