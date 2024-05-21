package com.campusconnect.backend.board.dto.response;

import com.campusconnect.backend.board.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BoardPagingListResponse {

    private List<BoardListResponse> boardListResponses = new ArrayList<>();
    private Long totalPage;
    private Long totalCount;

    @Builder
    public BoardPagingListResponse(List<BoardListResponse> boardListResponses,
                                   Long totalPage,
                                   Long totalCount) {
        this.boardListResponses = boardListResponses.stream()
                .map(board -> BoardListResponse.builder()
                        .boardId(board.getBoardId())
                        .department(board.getDepartment())
                        .name(board.getName())
                        .userProfileImage(board.getUserProfileImage())
                        .representativeImage(board.getRepresentativeImage())
                        .title(board.getTitle())
                        .favoriteCount(board.getFavoriteCount())
                        .commentCount(board.getCommentCount())
                        .tradeStatus(TradeStatus.valueOf(board.getTradeStatus()))
                        .build())
                .collect(Collectors.toList());
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }
}
