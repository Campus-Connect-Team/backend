package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardUpdateResponse {

    private Long boardId;
    private String name;
    private String department;
    private String title;
    private String content;
    private String tradeStatus;
    private String responseCode;

    @Builder
    public BoardUpdateResponse(Long boardId,
                               String name,
                               String department,
                               String title,
                               String content,
                               TradeStatus tradeStatus,
                               String responseCode) {
        this.boardId = boardId;
        this.name = name;
        this.department = department;
        this.title = title;
        this.content = content;
        this.tradeStatus = tradeStatus.getValue();
        this.responseCode = responseCode;
    }
}
