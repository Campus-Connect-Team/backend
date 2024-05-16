package com.campusconnect.backend.chat.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRequest {
    public enum ChatType {
        ENTER, TALK, LEAVE;
    }

    private ChatType chatType;
    private String chatRoomId;
    private String senderId;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isChecked;
}
