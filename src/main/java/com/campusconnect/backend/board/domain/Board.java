package com.campusconnect.backend.board.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
