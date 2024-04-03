package com.campusconnect.backend.chat.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import com.campusconnect.backend.board.domain.Board;
import com.campusconnect.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatRoom extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "deleted_by_seller")
    private Boolean isDeletedBySeller;

    @Column(name = "deleted_by_buyer")
    private Boolean isDeletedByBuyer;
}
