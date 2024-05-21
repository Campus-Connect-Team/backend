package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardReplyDetailResponse {

    // 답글 리스트
    private Long replyId;
    private String replierProfileImage;
    private String replierDepartment;
    private String replierName;
    private String replyContent;
    private LocalDateTime modifiedAt;

    @Builder
    public BoardReplyDetailResponse(Long replyId,
                                    String replierProfileImage,
                                    String replierDepartment,
                                    String replierName,
                                    String replyContent,
                                    LocalDateTime modifiedAt) {
        this.replyId = replyId;
        this.replierProfileImage = replierProfileImage;
        this.replierDepartment = replierDepartment;
        this.replierName = replierName;
        this.replyContent = replyContent;
        this.modifiedAt = modifiedAt;
    }
}
