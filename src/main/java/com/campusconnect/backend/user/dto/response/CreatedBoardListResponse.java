package com.campusconnect.backend.user.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class CreatedBoardListResponse {

    private Long boardId;
    private String tradeStatus;
    private String boardTitle;

    @Builder
    public CreatedBoardListResponse(Long boardId,
                                    TradeStatus tradeStatus,
                                    String boardTitle) {
        this.boardId = boardId;
        this.tradeStatus = tradeStatus.getValue();
        this.boardTitle = boardTitle;
    }
}
