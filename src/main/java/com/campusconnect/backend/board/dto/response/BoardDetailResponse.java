package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDetailResponse {

    private Long boardId;
    private String image;
    private String department;
    private String name;
    private LocalDateTime createDate;
    private Double sellerManner;
    private List<String> boardImages;
    private String title;
    private String content;
    private TradeStatus tradeStatus;
    private Integer favoriteCount;
    private Integer chatCount;

    @Builder
    public BoardDetailResponse(Long boardId,
                               String image,
                               String department,
                               String name,
                               LocalDateTime createDate,
                               Double sellerManner,
                               List<String> boardImages,
                               String title,
                               String content,
                               TradeStatus tradeStatus,
                               Integer favoriteCount,
                               Integer chatCount) {
        this.boardId = boardId;
        this.image = image;
        this.department = department;
        this.name = name;
        this.createDate = createDate;
        this.sellerManner = sellerManner;
        this.boardImages = boardImages;
        this.title = title;
        this.content = content;
        this.tradeStatus = tradeStatus;
        this.favoriteCount = favoriteCount;
        this.chatCount = chatCount;
    }
}
