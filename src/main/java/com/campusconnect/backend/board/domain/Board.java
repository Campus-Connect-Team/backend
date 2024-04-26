package com.campusconnect.backend.board.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.user.domain.User;
import com.campusconnect.backend.util.exception.CustomException;
import com.campusconnect.backend.util.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> boardImage;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status")
    private TradeStatus tradeStatus;

    @Column(name = "favorite_count")
    private Integer favoriteCount;

    @Column(name = "chat_count")
    private Integer chatCount;

    @Builder
    public Board(String title,
                 String content,
                 List<BoardImage> boardImage,
                 User user) {
        this.title = title;
        this.content = content;
        this.boardImage = new ArrayList<>();
        this.user = user;
        this.tradeStatus = TradeStatus.TRADE_ACTIVATION;
        this.favoriteCount = 0;
        this.chatCount = 0;
    }

    /** 제약사항 1 : 게시글에 대한 사진은 10장까지만 업로드 가능 */
    public Integer checkImageCount() {
        return boardImage.size();
    }

    /** 제약사항 2 : "거래 완료" 상태의 게시글은 수정 불가 */
    public void checkToBoardUpdateWithTradeStatus() {
        if (tradeStatus == TradeStatus.TRADE_COMPLETION) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_BOARD);
        }
    }

    /** 제약사항 3 : "거래 완료" 상태의 게시글은 수정 불가 */
    public void checkToBoardDeleteWithTradeStatus() {
        if (tradeStatus == TradeStatus.TRADE_COMPLETION) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_BOARD);
        }
    }

    /** 게시글 제목 변경 */
    public void updateTitle(String title) {
        this.title = title;
    }

    /** 게시글 내용(본문) 변경 */
    public void updateContent(String content) {
        this.content = content;
    }

    public void updateBoardImages(List<BoardImage> updatedImages) {
        this.boardImage = updatedImages;
    }

    /** 거래상태 변경 (거래 가능 -> 거래 완료) */
    public void changeTradeStatus(String tradeStatus) {
        if (tradeStatus == null) {
            this.tradeStatus = TradeStatus.TRADE_ACTIVATION;
        } else {
            this.tradeStatus = TradeStatus.TRADE_COMPLETION;
        }
    }

    /** (특정 게시글 조회 시) 업로드된 모든 상품 이미지 리스트 반환 */
    public List<String> getBoardImages() {
        return boardImage.stream()
                .map(BoardImage::getBoardImage)
                .collect(Collectors.toList());
    }

    public List<BoardImage> getBoardImagesToBoardImageType() {
        return boardImage.stream()
                .collect(Collectors.toList());
    }

    /** 관심 수 증가 */
    public void increaseFavoriteCount() {
        this.favoriteCount++;
    }

    /** 관심 수 감소 */
    public void decreaseFavoriteCount() {
        this.favoriteCount--;
    }

}
