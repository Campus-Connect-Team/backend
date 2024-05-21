package com.campusconnect.backend.comment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardCommentResponse {

    private Long commentId;
    private String commenterProfileImage;
    private String commenterDepartment;
    private String commenterName;
    private String commentContent;
    private LocalDateTime modifiedAt;

    @Builder
    public BoardCommentResponse(Long commentId,
                                String commenterProfileImage,
                                String commenterDepartment,
                                String commenterName,
                                String commentContent,
                                LocalDateTime modifiedAt) {
        this.commentId = commentId;
        this.commenterProfileImage = commenterProfileImage;
        this.commenterDepartment = commenterDepartment;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.modifiedAt = modifiedAt;
    }
}
