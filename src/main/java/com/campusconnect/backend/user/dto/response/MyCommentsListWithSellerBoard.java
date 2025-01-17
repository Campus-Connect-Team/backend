package com.campusconnect.backend.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class MyCommentsListWithSellerBoard {

    private Integer createdCommentCount;  // 해당 게시글에 작성한 댓글 건수
    private Long boardId;
    private String sellerProfileImage;
    private String sellerName;
    private String sellerDepartment;
    private String boardTitle;

    @Builder
    public MyCommentsListWithSellerBoard(Integer createdCommentCount,
                          Long boardId,
                          String sellerProfileImage,
                          String sellerName,
                          String sellerDepartment,
                          String boardTitle) {
        this.createdCommentCount = createdCommentCount;
        this.boardId = boardId;
        this.sellerProfileImage = sellerProfileImage;
        this.sellerName = sellerName;
        this.sellerDepartment = sellerDepartment;
        this.boardTitle = boardTitle;
    }
}
