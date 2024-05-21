package com.campusconnect.backend.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardCommentDetailResponse {

    // 댓글 리스트
    private Long commentId;
    private String commenterProfileImage;
    private String commenterDepartment;
    private String commenterName;
    private String commentContent;
    private LocalDateTime modifiedAt;

    // 댓글에 연관된 답글 리스트
    private List<BoardReplyDetailResponse> boardReplyDetailResponses;

    @Builder
    public BoardCommentDetailResponse(Long commentId,
                                      String commenterProfileImage,
                                      String commenterDepartment,
                                      String commenterName,
                                      String commentContent,
                                      LocalDateTime modifiedAt,
                                      List<BoardReplyDetailResponse> boardReplyDetailResponses) {
        this.commentId = commentId;
        this.commenterProfileImage = commenterProfileImage;
        this.commenterDepartment = commenterDepartment;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.modifiedAt = modifiedAt;
        this.boardReplyDetailResponses = boardReplyDetailResponses;
    }
}
