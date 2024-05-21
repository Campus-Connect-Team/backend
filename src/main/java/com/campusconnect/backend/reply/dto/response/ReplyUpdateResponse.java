package com.campusconnect.backend.reply.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyUpdateResponse {

    private Long replyId;
    private String replierUserProfile;
    private String replierDepartment;
    private String replierName;
    private String replyContent;
    private LocalDateTime modifiedAt;
    private String responseCode;

    @Builder
    public ReplyUpdateResponse(Long replyId,
                               String replierUserProfile,
                               String replierDepartment,
                               String replierName,
                               String replyContent,
                               LocalDateTime modifiedAt,
                               String responseCode) {
        this.replyId = replyId;
        this.replierUserProfile = replierUserProfile;
        this.replierDepartment = replierDepartment;
        this.replierName = replierName;
        this.replyContent = replyContent;
        this.modifiedAt = modifiedAt;
        this.responseCode = responseCode;
    }
}
