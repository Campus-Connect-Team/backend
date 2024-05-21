package com.campusconnect.backend.comment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentUpdateResponse {

    private Long commentId;
    private String commenterUserProfile;
    private String commenterDepartment;
    private String commenterName;
    private String commentContent;
    private LocalDateTime modifiedAt;
    private String responseCode;

    @Builder
    public CommentUpdateResponse(Long commentId,
                                 String commenterDepartment,
                                 String commenterName,
                                 String commentContent,
                                 LocalDateTime modifiedAt,
                                 String responseCode) {
        this.commentId = commentId;
        this.commenterDepartment = commenterDepartment;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.modifiedAt = modifiedAt;
        this.responseCode = responseCode;
    }
}
