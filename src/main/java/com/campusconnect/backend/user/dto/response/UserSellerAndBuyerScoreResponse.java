package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class UserSellerAndBuyerScoreResponse {

    private Double sellerManner;
    private Double buyerManner;
    private Integer tradeCompletedBoardsCount;  // 거래 완료 건수 (구매 완료 건수에 대한 필드도 추후 정의 필요)
    private String boardTradeStatus;
    private String name;
    private String department;
    private String title;

    @Builder
    public UserSellerAndBuyerScoreResponse(Double sellerManner,
                                           Double buyerManner,
                                           Integer tradeCompletedBoardsCount,
                                           String boardTradeStatus,
                                           String name,
                                           String department,
                                           String title) {
        this.sellerManner = sellerManner;
        this.buyerManner = buyerManner;
        this.tradeCompletedBoardsCount = tradeCompletedBoardsCount;
        this.boardTradeStatus = boardTradeStatus;
        this.name = name;
        this.department = department;
        this.title = title;
    }
}
