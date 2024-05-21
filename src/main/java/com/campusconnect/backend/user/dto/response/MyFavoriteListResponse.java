package com.campusconnect.backend.user.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class MyFavoriteListResponse {

    private String sellerImage;
    private String sellerDepartment;
    private String sellerName;
    private Integer favoriteCount;
    private Integer commentCount;
    private String boardTitle;
    private String tradeStatus;

    @Builder
    public MyFavoriteListResponse(String sellerImage,
                                  String sellerDepartment,
                                  String sellerName,
                                  Integer favoriteCount,
                                  Integer commentCount,
                                  String boardTitle,
                                  TradeStatus tradeStatus) {
        this.sellerImage = sellerImage;
        this.sellerDepartment = sellerDepartment;
        this.sellerName = sellerName;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.boardTitle = boardTitle;
        this.tradeStatus = tradeStatus.getValue();
    }
}
