package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.BoardImage;
import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BoardDetailResponse {

    private Long boardId;
    private String department;
    private String name;
    private Double sellerManner;
    private List<String> boardImages;
    private String title;
    private String content;
    private TradeStatus tradeStatus;
    private Integer favoriteCount;
    private Integer chatCount;

    @Builder
    public BoardDetailResponse(Long boardId,
                               String department,
                               String name,
                               Double sellerManner,
                               List<String> boardImages,
                               String title,
                               String content,
                               TradeStatus tradeStatus,
                               Integer favoriteCount,
                               Integer chatCount) {
        this.boardId = boardId;
        this.department = department;
        this.name = name;
        this.sellerManner = sellerManner;
        this.boardImages = boardImages;
        this.title = title;
        this.content = content;
        this.tradeStatus = tradeStatus;
        this.favoriteCount = favoriteCount;
        this.chatCount = chatCount;
    }
}
