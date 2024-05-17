package com.campusconnect.backend.chat.domain;

import com.campusconnect.backend.basetime.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_content")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatContent extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "chat_check")
    private Boolean isChecked;

    @Builder
    public ChatContent(ChatRoom chatRoom, Long senderId, String message, Boolean isChecked) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.message = message;
        this.isChecked = isChecked;
    }
}
