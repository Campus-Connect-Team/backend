package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import com.campusconnect.backend.comment.dto.response.BoardCommentResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDetailResponse {

    // 게시글에 대한 정보, 판매자 정보
    private Long boardId;
    private String image;
    private String department;
    private String name;
    private LocalDateTime createdAt;
    private List<String> boardImages;
    private String title;
    private String content;
    private String tradeStatus;
    private Integer favoriteCount;
    private Integer commentCount;

    // 댓글, 답글 리스트
    private List<BoardCommentDetailResponse> boardCommentDetailResponses;

    @Builder
    public BoardDetailResponse(Long boardId,
                               String image,
                               String department,
                               String name,
                               LocalDateTime createdAt,
                               List<String> boardImages,
                               String title,
                               String content,
                               TradeStatus tradeStatus,
                               Integer favoriteCount,
                               Integer commentCount,
                               List<BoardCommentDetailResponse> boardCommentDetailResponses) {
        this.boardId = boardId;
        this.image = image;
        this.department = department;
        this.name = name;
        this.createdAt = createdAt;
        this.boardImages = boardImages;
        this.title = title;
        this.content = content;
        this.tradeStatus = tradeStatus.getValue();
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.boardCommentDetailResponses = boardCommentDetailResponses;
    }
}
