package com.campusconnect.backend.chat.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRequest {

    private String chatRoomId;
    private String senderId;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isChecked;

    @Builder
    public ChatRequest(String chatRoomId, String senderId, String message, LocalDateTime createdAt, Boolean isChecked) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.isChecked = isChecked;
    }
}
