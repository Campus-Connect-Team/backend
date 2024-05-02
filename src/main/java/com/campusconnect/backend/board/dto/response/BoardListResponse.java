package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import com.campusconnect.backend.util.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListResponse {

    private Long boardId;
    private String department;
    private String name;
    private LocalDateTime createDate;
    private String userProfileImage;
    private String representativeImage;
    private String title;
    private Integer favoriteCount;
    private Integer chatCount;
    private TradeStatus tradeStatus;

    @Builder
    public BoardListResponse(Long boardId,
                             String department,
                             String name,
                             LocalDateTime createDate,
                             String userProfileImage,
                             String representativeImage,
                             String title,
                             Integer favoriteCount,
                             Integer chatCount,
                             TradeStatus tradeStatus) {
        this.boardId = boardId;
        this.department = department;
        this.name = name;
        this.createDate = createDate;
        this.userProfileImage = userProfileImage;
        this.representativeImage = representativeImage;
        this.title = title;
        this.favoriteCount = favoriteCount;
        this.chatCount = chatCount;
        this.tradeStatus = tradeStatus;
    }
}
